package mutsa.yewon.talksparkbe.domain.game.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomJoinRequest;
import mutsa.yewon.talksparkbe.domain.game.service.GameService;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketRoomController {

    private final RoomService roomService;
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 방 입장: /app/room/{roomId}/join
     * roomUpdate → /topic/room/{roomId}/update
     */
    @MessageMapping("/room/{roomId}/join")
    public void joinRoom(@DestinationVariable Long roomId,
                         @Payload RoomJoinRequest request,
                         StompHeaderAccessor accessor) {
        String token = (String) accessor.getSessionAttributes().get("accessToken");
        request.setRoomId(roomId);
        request.setAccessToken("Bearer " + token);

        roomService.joinRoom(request);

        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/update",
                roomService.getParticipantList(roomId)
        );
    }

    /**
     * 방 퇴장: /app/room/{roomId}/leave
     * roomUpdate → /topic/room/{roomId}/update
     */
    @MessageMapping("/room/{roomId}/leave")
    public void leaveRoom(@DestinationVariable Long roomId,
                          @Payload RoomJoinRequest request,
                          StompHeaderAccessor accessor) {
        String token = (String) accessor.getSessionAttributes().get("accessToken");
        request.setRoomId(roomId);
        request.setAccessToken("Bearer " + token);

        roomService.leaveRoom(request);

        if (roomService.getParticipateCount(roomId) <= 0) {
            gameService.removeGameState(roomId);
        }

        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/update",
                roomService.getParticipantList(roomId)
        );
    }

    /**
     * 게임 시작: /app/room/{roomId}/start
     * startGame → /topic/room/{roomId}/started
     */
    @MessageMapping("/room/{roomId}/start")
    public void startGame(@DestinationVariable Long roomId,
                          StompHeaderAccessor accessor) {
        String token = (String) accessor.getSessionAttributes().get("accessToken");
        roomService.changeStarted(roomId, "Bearer " + token);

        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/started",
                "게임이 시작됩니다."
        );
    }

    /**
     * 메시지 처리 예외 → /queue/errors (개인)
     */
    @MessageExceptionHandler
    public void handleException(Exception e, Principal principal) {
        log.error("WebSocketRoomController 예외: {}", e.getMessage(), e);

        String errorMessage = (e instanceof CustomTalkSparkException ce)
                ? ce.getErrorCode().getMessage()
                : e.getMessage();

        if (principal != null) {
            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors",
                    Map.of("error", errorMessage));
        }
    }
}