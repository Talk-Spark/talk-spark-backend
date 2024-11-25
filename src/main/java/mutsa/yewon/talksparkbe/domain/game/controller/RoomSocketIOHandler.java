package mutsa.yewon.talksparkbe.domain.game.controller;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomCreateRequest;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomJoinRequest;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomSocketIOHandler {

    private final SocketIOServer server;
    private final RoomService roomService;

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
                Long roomId = data.getRoomId();
                server.getRoomOperations(roomId.toString()).sendEvent("startGame", "게임이 시작됩니다.");
            } catch (Exception e) {
                System.err.println("게임 시작 오류: " + e.getMessage());
                client.sendEvent("startGameError", "게임 시작 중 오류가 발생했습니다.");
            }
        });

    }

}