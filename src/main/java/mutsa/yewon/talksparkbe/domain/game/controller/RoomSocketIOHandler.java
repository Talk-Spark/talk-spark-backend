package mutsa.yewon.talksparkbe.domain.game.controller;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mutsa.yewon.talksparkbe.domain.game.controller.request.AnswerSubmitRequest;
import mutsa.yewon.talksparkbe.domain.game.controller.request.GameStartRequest;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomCreateRequest;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomJoinRequest;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardQuestion;
import mutsa.yewon.talksparkbe.domain.game.service.GameService;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomSocketIOHandler {

    private final SocketIOServer server;
    private final RoomService roomService;
    private final GameService gameService;

    @PostConstruct
    public void startServer() {
        server.addConnectListener(client -> {
            System.out.println("Client connected: " + client.getSessionId());
        });

        server.addEventListener("message", String.class, (client, data, ackSender) -> {
            System.out.println("Received: " + data);
            client.sendEvent("response", "Hello, client!");
        });

        server.start();
    }

    @PreDestroy
    public void stopServer() {
        try {
            if (server != null) {
                server.stop();
                log.info("Socket.IO 서버가 성공적으로 종료되었습니다.");
            } else {
                log.warn("Socket.IO 서버 인스턴스가 null입니다.");
            }
        } catch (Exception e) {
            log.error("Socket.IO 서버 종료 중 오류 발생", e);
        }
    }

    @PostConstruct
    public void startListeners() {
        server.addEventListener("createRoom", RoomCreateRequest.class, (client, data, ackSender) -> {
            Long roomId = roomService.createRoom(data);
            client.sendEvent("roomCreated", roomId); // 생성된 방 ID 반환
        });

        server.addEventListener("getRooms", Void.class, (client, data, ackSender) -> {
            System.out.println("방 목록 줘야겠다");
            client.sendEvent("roomList", roomService.listAllRooms());
        });

        server.addEventListener("joinRoom", RoomJoinRequest.class, (client, data, ackSender) -> {
            try {
                roomService.joinRoom(data);
                server.getClient(client.getSessionId()).joinRoom(data.getRoomId().toString());
                server.getRoomOperations(data.getRoomId().toString()).sendEvent("roomUpdate", roomService.getParticipantList(data.getRoomId()));
            } catch (CustomTalkSparkException ce) {
                System.err.println("방 참가 중 서비스 오류 발생: " + ce.getErrorCode().getMessage());
                client.sendEvent("roomJoinError", "서비스 코드 때문에 방에 참가할 수 없습니다. 다시 시도해주세요.");
            } catch (Exception e) {
                System.err.println("방 참가 중 오류 발생: " + e.getMessage());
                client.sendEvent("roomJoinError", "방에 참가할 수 없습니다. 다시 시도해주세요.");
            }
        });

        server.addEventListener("leaveRoom", RoomJoinRequest.class, (client, data, ackSender) -> {
            try {
                System.out.println("data 에서 액세스토큰 = " + data.getAccessToken());
                roomService.leaveRoom(data);
                server.getClient(client.getSessionId()).leaveRoom(data.getRoomId().toString());
                server.getRoomOperations(data.getRoomId().toString()).sendEvent("roomUpdate", roomService.getParticipantList(data.getRoomId()));
            } catch (CustomTalkSparkException ce) {
                client.sendEvent("roomLeaveError", "서비스 코드 때문에 방에서 퇴장할 수 없습니다. 다시 시도해주세요.");
            } catch (Exception e) {
                client.sendEvent("roomLeaveError", "방에서 퇴장할 수 없습니다. 다시 시도해주세요.");
            }
        });

        server.addEventListener("startGame", RoomJoinRequest.class, (client, data, ackSender) -> {
            try {
                // TODO: 방 디비에 시작처리
                Long roomId = data.getRoomId();
                server.getRoomOperations(roomId.toString()).sendEvent("startGame", "게임이 시작됩니다.");
            } catch (Exception e) {
                System.err.println("게임 시작 오류: " + e.getMessage());
                client.sendEvent("startGameError", "게임 시작 중 오류가 발생했습니다.");
            }
        });

    }

    /* ================ 게임 관련 ================ */

    @PostConstruct
    public void startGameListeners() {
        server.addEventListener("joinGame", RoomJoinRequest.class, (client, data, ackSender) -> {
            server.getClient(client.getSessionId()).joinRoom(data.getRoomId().toString());
            client.sendEvent("gameJoined", "게임 참가 완료");
        });

        server.addEventListener("prepareQuizzes", GameStartRequest.class, (client, data, ackSender) -> {
            System.out.println("퀴즈를 준비합니다.");
            Long roomId = data.getRoomId();
            gameService.startGame(roomId);
            broadcastNextQuestion(roomId);
        });

        server.addEventListener("submit", AnswerSubmitRequest.class, (client, data, ackSender) -> {
            Long roomId = data.getRoomId();
            Long sparkUserId = data.getSparkUserId();
            String answer = data.getAnswer();

            String next = gameService.submitAnswer(roomId, sparkUserId, answer);
            if (next.equals("next")) {
                broadcastNextQuestion(roomId);
            }
        });
    }

    // 다음 질문 브로드캐스트 메서드
    private void broadcastNextQuestion(Long roomId) {
        CardQuestion nextQuestion = gameService.getNextQuestion(roomId);
        System.out.println("nextQuestion = " + nextQuestion);
        if (nextQuestion != null) {
            server.getRoomOperations(roomId.toString()).sendEvent("question", gameService.getCurrentCard(roomId), nextQuestion);
        }
    }

}