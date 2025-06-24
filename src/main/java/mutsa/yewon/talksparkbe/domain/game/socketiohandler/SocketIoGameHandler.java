package mutsa.yewon.talksparkbe.domain.game.socketiohandler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.controller.request.AnswerSubmitRequest;
import mutsa.yewon.talksparkbe.domain.game.controller.request.GameStartRequest;
import mutsa.yewon.talksparkbe.domain.game.controller.request.QuestionRequest;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomJoinRequest;
import mutsa.yewon.talksparkbe.domain.game.entity.QuestionTip;
import mutsa.yewon.talksparkbe.domain.game.service.GameService;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import mutsa.yewon.talksparkbe.domain.game.service.dto.AnswerDto;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardQuestion;
import mutsa.yewon.talksparkbe.domain.game.service.dto.SwitchSubject;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SocketIoGameHandler {

    private final GameService gameService;
    private final RoomService roomService;
    private final SocketIOServer server;
    private final JWTUtil jwtUtil;
    private final SparkUserRepository sparkUserRepository;

    @PostConstruct
    public void registerListeners() {
        server.addEventListener("joinGame", RoomJoinRequest.class, this::joinGame);
        server.addEventListener("prepareQuizzes", GameStartRequest.class, this::prepareQuizzes);
        server.addEventListener("getQuestion", QuestionRequest.class, this::getQuestion);
        server.addEventListener("submitSelection", AnswerSubmitRequest.class,this::submitAnswer);
        server.addEventListener("resume", RoomJoinRequest.class, this::resumeGame);
    }

    private void resumeGame(SocketIOClient socketIOClient, RoomJoinRequest joinRequest, AckRequest ackRequest) {
        try {
            Long roomId = joinRequest.getRoomId();
            
            // JWT 토큰 검증 및 Bearer 제거
            String token = joinRequest.getAccessToken();
            if (token != null && token.startsWith("Bearer ")) {
                token = token.replace("Bearer ", "");
            }
            Map<String, Object> userInfo = jwtUtil.validateToken(token);
            Long sparkUserId = (Long) userInfo.get("sparkUserId");

            // 유저가 해당 방에 참여하고 있는지 확인
            if (roomService.isUserInRoom(sparkUserId, roomId)) {
                // 소켓 룸에 재참여
                server.getClient(socketIOClient.getSessionId()).joinRoom(roomId.toString());

                // 게임이 시작되었는지 확인 (GameService의 gameStates 맵 존재 여부로 확인)
                try {
                    boolean isGameStarted = gameService.getCurrentCard(roomId) != null;
                    
                    if (isGameStarted) {
                        // 게임 진행 중인 경우 - 현재 게임 상태 복구
                        resumeInProgressGame(socketIOClient, roomId, sparkUserId);
                    } else {
                        // 대기실인 경우 - 대기실 상태 복구
                        resumeWaitingRoom(socketIOClient, roomId, sparkUserId);
                    }
                } catch (Exception e) {
                    // 게임이 시작되지 않은 경우 대기실로 간주
                    resumeWaitingRoom(socketIOClient, roomId, sparkUserId);
                }
            } else {
                socketIOClient.sendEvent("resumeError", "해당 방에 참여하지 않은 사용자입니다.");
            }
        } catch (Exception e) {
            socketIOClient.sendEvent("resumeError", "게임 재참여 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 진행 중인 게임 상태 복구
     */
    private void resumeInProgressGame(SocketIOClient socketIOClient, Long roomId, Long sparkUserId) {
        try {
            // 현재 게임 진행 상태 정보 (기존 broadcastQuestion과 동일한 정보 활용)
            CardQuestion currentQuestion = gameService.getQuestion(roomId);
            String roomName = roomService.getRoomName(roomId);
            
            // 기본 게임 상태 데이터 (broadcastQuestion에서 사용하는 패턴과 동일)
            Map<String, Object> gameResumeData = Map.of(
                "roomId", roomId,
                "roomName", roomName,
                "currentCard", gameService.getCurrentCard(roomId),
                "currentBlanks", gameService.getCurrentCardBlanks(roomId),
                "question", currentQuestion,
                "questionTip", gameService.getQuestionTip(roomId, currentQuestion.getFieldName()),
                "allAnswered", gameService.allPeopleSubmitted(roomId)
            );

            socketIOClient.sendEvent("resumeInGame", gameResumeData);
            
        } catch (Exception e) {
            socketIOClient.sendEvent("resumeError", "게임 상태 복구 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 대기실 상태 복구
     */
    private void resumeWaitingRoom(SocketIOClient socketIOClient, Long roomId, Long sparkUserId) {
        try {
            SparkUser sparkUser = sparkUserRepository.findById(sparkUserId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
            
            // 대기실 상태 데이터 구성
            Map<String, Object> waitingRoomData = Map.of(
                "roomDetails", roomService.getRoomDetails(roomId),
                "participants", roomService.getParticipantList(roomId),
                "currentPeople", roomService.getParticipateCount(roomId),
                "isHost", roomService.checkHost(roomId, sparkUser)
            );

            socketIOClient.sendEvent("resumeInWaitingRoom", waitingRoomData);
            
        } catch (Exception e) {
            socketIOClient.sendEvent("resumeError", "대기실 상태 복구 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    private void joinGame(SocketIOClient client, RoomJoinRequest data, AckRequest ackRequest) {
        server.getClient(client.getSessionId()).joinRoom(data.getRoomId().toString());

        String token = data.getAccessToken();
        String jwt = token.replace("Bearer ", "");
        Map<String, Object> claims = jwtUtil.validateToken(jwt);
        String kakaoId = (String) claims.get("kakaoId");
        SparkUser sparkUser = sparkUserRepository.findByKakaoId(kakaoId).orElseThrow(() -> new RuntimeException("유저 못찾음"));

        client.sendEvent("gameJoined", sparkUser.getId());
    }

    private void prepareQuizzes(SocketIOClient socketIOClient, GameStartRequest data, AckRequest ackRequest) {
        gameService.startGame(data.getRoomId());
    }

    private void getQuestion(SocketIOClient socketIOClient, QuestionRequest data, AckRequest ackRequest) {
        broadcastQuestion(data.getRoomId());
        questionTip(data.getRoomId());
    }

    private void submitAnswer(SocketIOClient socketIOClient, AnswerSubmitRequest data, AckRequest ackRequest) {
        Long roomId = data.getRoomId();
        Long sparkUserId = data.getSparkUserId();
        String answer = data.getAnswer();

        gameService.submitAnswer(roomId, sparkUserId, answer);
        if (gameService.allPeopleSubmitted(roomId)) {
            broadcastSingleQuestionResult(roomId);
            gameService.updateBlanks(roomId);
        }
    }

    private void loadNextQuestion(SocketIOClient socketIOClient, QuestionRequest data, AckRequest ackRequest) {
        Long roomId = data.getRoomId();

        SwitchSubject switchSubject = gameService.isSwitchingSubject(roomId);

        switch (switchSubject){
            case END -> sendLastResult(roomId);
            case TRUE -> {
                sendSingleResult(roomId);
                gameService.switchCurrentPlayerId(roomId);
            }
            default ->  {
                gameService.loadNextQuestion(roomId);
                broadcastQuestion(roomId);
            }

        }
    }

    // 질문 브로드캐스트 메서드
    private void broadcastQuestion(Long roomId) {
        CardQuestion question = gameService.getQuestion(roomId);
        String roomName = roomService.getRoomName(roomId);
        server.getRoomOperations(roomId.toString()).sendEvent("question",
                gameService.getCurrentCard(roomId), gameService.getCurrentCardBlanks(roomId), question, roomName);
    }

    private void questionTip(Long roomId) {
        String field = gameService.getQuestion(roomId).getFieldName();
        System.out.println(gameService.getQuestionTip(roomId, field));
        server.getRoomOperations(roomId.toString()).sendEvent("questionTip", gameService.getQuestionTip(roomId, field));
    }

    private void broadcastSingleQuestionResult(Long roomId) {
        List<AnswerDto> singleQuestionScoreBoard = gameService.getSingleQuestionScoreBoard(roomId);

        if (!singleQuestionScoreBoard.isEmpty())
            server.getRoomOperations(roomId.toString()).sendEvent("singleQuestionScoreBoard", singleQuestionScoreBoard);
    }

    private void sendSingleResult(Long roomId) {
        server.getRoomOperations(roomId.toString()).sendEvent("singleResult",
                gameService.getCurrentCard(roomId));
    }

    private void sendLastResult(Long roomId) {
        server.getRoomOperations(roomId.toString()).sendEvent("lastResult",
                gameService.getCurrentCard(roomId));
    }
}
