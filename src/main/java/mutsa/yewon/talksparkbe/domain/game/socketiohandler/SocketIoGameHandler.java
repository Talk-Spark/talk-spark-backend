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
        server.addEventListener("next", QuestionRequest.class, this::loadNextQuestion);
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
