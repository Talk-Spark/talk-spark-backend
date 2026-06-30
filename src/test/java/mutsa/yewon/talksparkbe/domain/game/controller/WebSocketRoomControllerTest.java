package mutsa.yewon.talksparkbe.domain.game.controller;

import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomJoinRequest;
import mutsa.yewon.talksparkbe.domain.game.service.GameService;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse.RoomParticipantResponse;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketRoomControllerTest {

    @Mock
    private RoomService roomService;

    @Mock
    private GameService gameService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketRoomController controller;

    private StompHeaderAccessor accessor;
    private Map<String, Object> sessionAttrs;

    @BeforeEach
    void setUp() {
        accessor = mock(StompHeaderAccessor.class);
        sessionAttrs = new HashMap<>();
        sessionAttrs.put("accessToken", "test-token-value");
        sessionAttrs.put("sparkUserId", 1L);
    }

    @Test
    @DisplayName("joinRoom 정상 처리 시 참가자 목록을 브로드캐스트한다")
    void joinRoom_정상처리시_참가자목록을_브로드캐스트한다() {
        // given
        Long roomId = 1L;
        RoomJoinRequest request = new RoomJoinRequest();

        List<RoomParticipantResponse> participants = List.of(
                RoomParticipantResponse.builder()
                        .sparkUserId(1L)
                        .name("테스트유저")
                        .color("BLUE")
                        .isOwner(true)
                        .build()
        );
        given(accessor.getSessionAttributes()).willReturn(sessionAttrs);
        willDoNothing().given(roomService).joinRoom(any(RoomJoinRequest.class));
        given(roomService.getParticipantList(roomId)).willReturn(participants);

        // when
        controller.joinRoom(roomId, request, accessor);

        // then
        verify(roomService).joinRoom(argThat(r -> r.getRoomId().equals(roomId)
                && r.getAccessToken().equals("Bearer test-token-value")));
        verify(messagingTemplate).convertAndSend(
                eq("/topic/room/" + roomId + "/update"),
                eq(participants)
        );
    }

    @Test
    @DisplayName("joinRoom 방이 꽉 찼을 때 예외 발생 시 에러 큐에 전송한다")
    void joinRoom_방이_꽉_찼을때_에러큐에_전송한다() {
        // given
        Long roomId = 1L;
        CustomTalkSparkException roomFullException = new CustomTalkSparkException(ErrorCode.ROOM_FULL);
        Principal principal = () -> "1";

        // when
        controller.handleException(roomFullException, principal);

        // then
        verify(messagingTemplate).convertAndSendToUser(
                eq("1"),
                eq("/queue/errors"),
                argThat(m -> m instanceof Map && ((Map<?, ?>) m).containsKey("error"))
        );
    }

    @Test
    @DisplayName("startGame 호출 시 시작 이벤트를 브로드캐스트한다")
    void startGame_호출시_시작_이벤트를_브로드캐스트한다() {
        // given
        Long roomId = 1L;
        given(accessor.getSessionAttributes()).willReturn(sessionAttrs);
        willDoNothing().given(roomService).changeStarted(eq(roomId), anyString());

        // when
        controller.startGame(roomId, accessor);

        // then
        verify(roomService).changeStarted(eq(roomId), eq("Bearer test-token-value"));
        verify(messagingTemplate).convertAndSend(
                eq("/topic/room/" + roomId + "/started"),
                eq("게임이 시작됩니다.")
        );
    }

    @Test
    @DisplayName("leaveRoom 정상 처리 시 참가자 목록을 브로드캐스트한다")
    void leaveRoom_정상처리시_참가자목록을_브로드캐스트한다() {
        // given
        Long roomId = 1L;
        RoomJoinRequest request = new RoomJoinRequest();

        given(accessor.getSessionAttributes()).willReturn(sessionAttrs);
        willDoNothing().given(roomService).leaveRoom(any(RoomJoinRequest.class));
        given(roomService.getParticipateCount(roomId)).willReturn(1);
        given(roomService.getParticipantList(roomId)).willReturn(List.of());

        // when
        controller.leaveRoom(roomId, request, accessor);

        // then
        verify(roomService).leaveRoom(argThat(r -> r.getRoomId().equals(roomId)
                && r.getAccessToken().equals("Bearer test-token-value")));
        verify(messagingTemplate).convertAndSend(
                eq("/topic/room/" + roomId + "/update"),
                eq(List.of())
        );
        verify(gameService, never()).removeGameState(anyLong());
    }

    @Test
    @DisplayName("leaveRoom 후 참가자가 0명이면 게임 상태를 제거한다")
    void leaveRoom_후_참가자없으면_게임상태_제거한다() {
        // given
        Long roomId = 1L;
        RoomJoinRequest request = new RoomJoinRequest();

        given(accessor.getSessionAttributes()).willReturn(sessionAttrs);
        willDoNothing().given(roomService).leaveRoom(any(RoomJoinRequest.class));
        given(roomService.getParticipateCount(roomId)).willReturn(0);
        given(roomService.getParticipantList(roomId)).willReturn(List.of());

        // when
        controller.leaveRoom(roomId, request, accessor);

        // then
        verify(gameService).removeGameState(roomId);
    }
}