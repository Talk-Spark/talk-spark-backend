package mutsa.yewon.talksparkbe.domain.game.socketiohandler;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mutsa.yewon.talksparkbe.domain.game.controller.request.*;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SockectIoRoomHandler {

    private final SocketIOServer server;
    private final RoomService roomService;

    @PostConstruct
    public void startServer() {
        server.addConnectListener(client -> {
            System.out.println("Client connected: " + client.getSessionId());
            client.sendEvent("connectAck", "당신은 서버와 연결되었습니다.");
        });

        server.addEventListener("message", String.class, (client, data, ackSender) -> {
            System.out.println("Received: " + data);
            client.sendEvent("response", "Hello, client!");
        });

        server.addDisconnectListener(client -> {
            client.sendEvent("disconnecting", "당신은 이제 연결이 끊깁니다.");
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
    public void startRoomListeners() {
        server.addEventListener("joinRoom", RoomJoinRequest.class, (client, data, ackSender) -> {
            try {
                System.out.println("joinRoom 받음. " + data.toString());
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
                System.out.println("leaveRoom 받음. " + data.toString());
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
                roomService.changeStarted(roomId);
                server.getRoomOperations(roomId.toString()).sendEvent("startGame", "게임이 시작됩니다.");
            } catch (Exception e) {
                client.sendEvent("startGameError", "게임 시작 중 오류가 발생했습니다.");
            }
        });
    }



}