package mutsa.yewon.talksparkbe.domain.game.controller;

import mutsa.yewon.talksparkbe.domain.game.controller.request.AnswerSubmitRequest;
import mutsa.yewon.talksparkbe.domain.game.service.GameService;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import mutsa.yewon.talksparkbe.domain.card.entity.CardThema;
import mutsa.yewon.talksparkbe.domain.game.controller.dto.QuestionBroadcastDTO;
import mutsa.yewon.talksparkbe.domain.game.service.dto.*;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WebSocketGameControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private RoomService roomService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private SparkUserRepository sparkUserRepository;

    @InjectMocks
    private WebSocketGameController controller;

    private Principal principal;
    private final Long roomId = 1L;
    private final Long sparkUserId = 100L;

    @BeforeEach
    void setUp() {
        principal = () -> sparkUserId.toString();
    }

    @Test
    @DisplayName("답 제출 후 미완료 상태이면 scoreboard를 브로드캐스트하지 않는다")
    void 답_제출_후_미완료시_scoreboard_브로드캐스트_안함() {
        // given
        AnswerSubmitRequest request = new AnswerSubmitRequest();
        request.setAnswer("정답");

        willDoNothing().given(gameService).submitAnswer(roomId, sparkUserId, "정답");
        given(gameService.allPeopleSubmitted(roomId)).willReturn(false);

        // when
        controller.submitAnswer(roomId, request, principal);

        // then
        verify(gameService).submitAnswer(roomId, sparkUserId, "정답");
        verify(messagingTemplate, never()).convertAndSend(
                eq("/topic/room/" + roomId + "/scoreboard"), any(Object.class)
        );
        verify(gameService, never()).updateBlanks(anyLong());
    }

    @Test
    @DisplayName("답 제출 후 전원 완료 시 scoreboard를 브로드캐스트하고 다음 상태를 처리한다")
    void 답_제출_후_전원완료시_scoreboard_브로드캐스트_함() {
        // given
        AnswerSubmitRequest request = new AnswerSubmitRequest();
        request.setAnswer("정답");

        List<AnswerDto> scoreBoard = List.of(
                AnswerDto.builder().sparkUserId(sparkUserId).name("테스트유저").isCorrect(true).color(CardThema.BLUE).build()
        );
        CardQuestion question = new CardQuestion(1L, sparkUserId, "name", "정답", List.of("정답", "오답"));
        CardResponseCustomDTO card = CardResponseCustomDTO.builder()
                .id(1L).name("테스트명함").userId(sparkUserId).build();

        willDoNothing().given(gameService).submitAnswer(roomId, sparkUserId, "정답");
        given(gameService.allPeopleSubmitted(roomId)).willReturn(true);
        given(gameService.getSingleQuestionScoreBoard(roomId)).willReturn(scoreBoard);
        willDoNothing().given(gameService).updateBlanks(roomId);
        given(gameService.isSwitchingSubject(roomId)).willReturn(SwitchSubject.FALSE);
        willDoNothing().given(gameService).loadNextQuestion(roomId);
        given(gameService.getQuestion(roomId)).willReturn(question);
        given(gameService.getCurrentCard(roomId)).willReturn(card);
        given(gameService.getCurrentCardBlanks(roomId)).willReturn(
                CardBlanksDto.of(sparkUserId, List.of("name"))
        );
        given(roomService.getRoomName(roomId)).willReturn("테스트방");
        given(gameService.getQuestionTip(roomId, "name")).willReturn("힌트");

        // when
        controller.submitAnswer(roomId, request, principal);

        // then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/room/" + roomId + "/scoreboard"),
                eq(scoreBoard)
        );
        verify(gameService).updateBlanks(roomId);
        verify(gameService).loadNextQuestion(roomId);
        verify(messagingTemplate).convertAndSend(
                eq("/topic/room/" + roomId + "/question"), any(QuestionBroadcastDTO.class)
        );
    }

    @Test
    @DisplayName("getQuestion 호출 시 question과 tip을 브로드캐스트한다")
    void getQuestion_호출시_question과_tip을_브로드캐스트한다() {
        // given
        CardQuestion question = new CardQuestion(1L, sparkUserId, "mbti", "INFP", List.of("INFP", "ENTP"));
        CardResponseCustomDTO card = CardResponseCustomDTO.builder()
                .id(1L).name("테스트명함").userId(sparkUserId).build();

        given(gameService.getQuestion(roomId)).willReturn(question);
        given(gameService.getCurrentCard(roomId)).willReturn(card);
        given(gameService.getCurrentCardBlanks(roomId)).willReturn(
                CardBlanksDto.of(sparkUserId, List.of("mbti"))
        );
        given(roomService.getRoomName(roomId)).willReturn("테스트방");
        given(gameService.getQuestionTip(roomId, "mbti")).willReturn("MBTI 힌트");

        // when
        controller.getQuestion(roomId);

        // then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/room/" + roomId + "/question"),
                any(QuestionBroadcastDTO.class)
        );
        verify(messagingTemplate).convertAndSend(
                eq("/topic/room/" + roomId + "/tip"),
                eq("MBTI 힌트")
        );
    }

    @Test
    @DisplayName("resume: 게임 진행 중이면 현재 상태를 개인 큐로 전송한다")
    void resume_게임진행중이면_현재상태를_개인큐로_전송한다() {
        // given
        CardQuestion question = new CardQuestion(1L, sparkUserId, "hobby", "축구", List.of("축구", "농구"));
        CardResponseCustomDTO card = CardResponseCustomDTO.builder()
                .id(1L).name("명함").userId(sparkUserId).build();

        given(gameService.isGameInProgress(roomId)).willReturn(true);
        given(gameService.getQuestion(roomId)).willReturn(question);
        given(roomService.getRoomName(roomId)).willReturn("테스트방");
        given(gameService.getCurrentCard(roomId)).willReturn(card);
        given(gameService.getCurrentCardBlanks(roomId)).willReturn(
                CardBlanksDto.of(sparkUserId, List.of("hobby"))
        );
        given(gameService.getQuestionTip(roomId, "hobby")).willReturn("취미 힌트");
        given(gameService.allPeopleSubmitted(roomId)).willReturn(false);

        // when
        controller.resume(roomId, principal);

        // then
        verify(messagingTemplate).convertAndSendToUser(
                eq(sparkUserId.toString()),
                eq("/queue/user"),
                any(Map.class)
        );
    }

    @Test
    @DisplayName("전원 제출 후 게임 종료(END) 시 lastResult를 브로드캐스트한다")
    void 전원_제출_후_게임종료시_lastResult를_브로드캐스트한다() {
        // given
        AnswerSubmitRequest request = new AnswerSubmitRequest();
        request.setAnswer("정답");

        List<AnswerDto> scoreBoard = List.of(
                AnswerDto.builder().sparkUserId(sparkUserId).name("테스트유저").isCorrect(true).color(CardThema.BLUE).build()
        );
        CardResponseCustomDTO card = CardResponseCustomDTO.builder()
                .id(1L).name("명함").userId(sparkUserId).build();

        willDoNothing().given(gameService).submitAnswer(roomId, sparkUserId, "정답");
        given(gameService.allPeopleSubmitted(roomId)).willReturn(true);
        given(gameService.getSingleQuestionScoreBoard(roomId)).willReturn(scoreBoard);
        willDoNothing().given(gameService).updateBlanks(roomId);
        given(gameService.isSwitchingSubject(roomId)).willReturn(SwitchSubject.END);
        given(gameService.getCurrentCard(roomId)).willReturn(card);

        // when
        controller.submitAnswer(roomId, request, principal);

        // then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/room/" + roomId + "/lastResult"),
                eq(card)
        );
    }
}