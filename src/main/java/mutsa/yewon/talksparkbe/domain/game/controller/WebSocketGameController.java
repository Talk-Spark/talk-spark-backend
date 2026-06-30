package mutsa.yewon.talksparkbe.domain.game.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mutsa.yewon.talksparkbe.domain.game.controller.dto.QuestionBroadcastDTO;
import mutsa.yewon.talksparkbe.domain.game.controller.request.AnswerSubmitRequest;
import mutsa.yewon.talksparkbe.domain.game.service.GameService;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import mutsa.yewon.talksparkbe.domain.game.service.dto.AnswerDto;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardQuestion;
import mutsa.yewon.talksparkbe.domain.game.service.dto.SwitchSubject;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketGameController {

    private final GameService gameService;
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final SparkUserRepository sparkUserRepository;

    /**
     * 게임 참가: /app/room/{roomId}/joinGame
     * gameJoined → /user/queue/user (개인)
     */
    @MessageMapping("/room/{roomId}/joinGame")
    public void joinGame(@DestinationVariable Long roomId, Principal principal) {
        Long sparkUserId = Long.parseLong(principal.getName());
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/user", sparkUserId);
    }

    /**
     * 퀴즈 준비: /app/room/{roomId}/prepare
     * gameReady → /topic/room/{roomId}/gameReady
     */
    @MessageMapping("/room/{roomId}/prepare")
    public void prepareQuizzes(@DestinationVariable Long roomId) {
        gameService.startGame(roomId);
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/gameReady",
                "퀴즈 준비가 완료되었습니다."
        );
    }

    /**
     * 문제 요청: /app/room/{roomId}/question
     * question → /topic/room/{roomId}/question
     * questionTip → /topic/room/{roomId}/tip
     */
    @MessageMapping("/room/{roomId}/question")
    public void getQuestion(@DestinationVariable Long roomId) {
        broadcastQuestion(roomId);
        broadcastQuestionTip(roomId);
    }

    /**
     * 답변 제출: /app/room/{roomId}/answer
     * singleQuestionScoreBoard → /topic/room/{roomId}/scoreboard
     * 전원 제출 시 자동으로 다음 상태 처리
     */
    @MessageMapping("/room/{roomId}/answer")
    public void submitAnswer(@DestinationVariable Long roomId,
                             @Payload AnswerSubmitRequest request,
                             Principal principal) {
        Long sparkUserId = Long.parseLong(principal.getName());
        String answer = request.getAnswer();

        gameService.submitAnswer(roomId, sparkUserId, answer);

        if (gameService.allPeopleSubmitted(roomId)) {
            broadcastSingleQuestionResult(roomId);
            gameService.updateBlanks(roomId);
            processNextGameState(roomId);
        }
    }

    /**
     * 게임 재접속: /app/room/{roomId}/resume
     * resumeInGame or resumeInWaitingRoom → /user/queue/user (개인)
     */
    @MessageMapping("/room/{roomId}/resume")
    public void resume(@DestinationVariable Long roomId, Principal principal) {
        Long sparkUserId = Long.parseLong(principal.getName());

        try {
            // 게임 진행 중인지 확인
            if (gameService.isGameInProgress(roomId)) {
                resumeInProgressGame(roomId, sparkUserId, principal.getName());
            } else {
                resumeWaitingRoom(roomId, sparkUserId, principal.getName());
            }
        } catch (Exception e) {
            log.error("resume 처리 중 오류 발생: {}", e.getMessage(), e);
            messagingTemplate.convertAndSendToUser(
                    principal.getName(), "/queue/errors",
                    Map.of("error", "게임 재참여 중 오류가 발생했습니다: " + e.getMessage())
            );
        }
    }

    @MessageExceptionHandler
    public void handleException(Exception e, Principal principal) {
        log.error("WebSocketGameController 예외: {}", e.getMessage(), e);

        if (principal != null) {
            messagingTemplate.convertAndSendToUser(
                    principal.getName(), "/queue/errors",
                    Map.of("error", e.getMessage())
            );
        }
    }

    // ─────────────────────────────────────────────
    // Private helper methods (기존 SocketIoGameHandler 로직 이관)
    // ─────────────────────────────────────────────

    private void broadcastQuestion(Long roomId) {
        CardQuestion question = gameService.getQuestion(roomId);
        String roomName = roomService.getRoomName(roomId);

        QuestionBroadcastDTO dto = QuestionBroadcastDTO.builder()
                .currentCard(gameService.getCurrentCard(roomId))
                .currentBlanks(gameService.getCurrentCardBlanks(roomId))
                .question(question)
                .roomName(roomName)
                .build();

        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/question", dto);
    }

    private void broadcastQuestionTip(Long roomId) {
        String field = gameService.getQuestion(roomId).getFieldName();
        String tip = gameService.getQuestionTip(roomId, field);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/tip", tip);
    }

    private void broadcastSingleQuestionResult(Long roomId) {
        List<AnswerDto> scoreBoard = gameService.getSingleQuestionScoreBoard(roomId);
        if (!scoreBoard.isEmpty()) {
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/scoreboard", scoreBoard);
        }
    }

    private void sendSingleResult(Long roomId) {
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/result",
                gameService.getCurrentCard(roomId)
        );
    }

    private void sendLastResult(Long roomId) {
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/lastResult",
                gameService.getCurrentCard(roomId)
        );
    }

    /**
     * allPeopleSubmitted() == true 인 경우 서버에서 자동으로 다음 상태 처리
     * (기존 클라이언트 → 서버 loadNextQuestion 이벤트를 서버 내부 로직으로 전환)
     */
    private void processNextGameState(Long roomId) {
        SwitchSubject switchSubject = gameService.isSwitchingSubject(roomId);

        switch (switchSubject) {
            case END -> sendLastResult(roomId);
            case TRUE -> {
                sendSingleResult(roomId);
                gameService.switchCurrentPlayerId(roomId);
            }
            default -> {
                gameService.loadNextQuestion(roomId);
                broadcastQuestion(roomId);
                broadcastQuestionTip(roomId);
            }
        }
    }

    private void resumeInProgressGame(Long roomId, Long sparkUserId, String userName) {
        CardQuestion currentQuestion = gameService.getQuestion(roomId);
        String roomName = roomService.getRoomName(roomId);

        Map<String, Object> gameResumeData = Map.of(
                "roomId", roomId,
                "roomName", roomName,
                "currentCard", gameService.getCurrentCard(roomId),
                "currentBlanks", gameService.getCurrentCardBlanks(roomId),
                "question", currentQuestion,
                "questionTip", gameService.getQuestionTip(roomId, currentQuestion.getFieldName()),
                "allAnswered", gameService.allPeopleSubmitted(roomId)
        );

        messagingTemplate.convertAndSendToUser(userName, "/queue/user", gameResumeData);
    }

    private void resumeWaitingRoom(Long roomId, Long sparkUserId, String userName) {
        SparkUser sparkUser = sparkUserRepository.findById(sparkUserId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Map<String, Object> waitingRoomData = Map.of(
                "roomDetails", roomService.getRoomDetails(roomId),
                "participants", roomService.getParticipantList(roomId),
                "currentPeople", roomService.getParticipateCount(roomId),
                "isHost", roomService.checkHost(roomId, sparkUser)
        );

        messagingTemplate.convertAndSendToUser(userName, "/queue/user", waitingRoomData);
    }
}