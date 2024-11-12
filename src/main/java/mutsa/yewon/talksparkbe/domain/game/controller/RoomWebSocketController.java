package mutsa.yewon.talksparkbe.domain.game.controller;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import mutsa.yewon.talksparkbe.domain.game.service.response.RoomParticipantResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RoomWebSocketController {

    private final RoomService roomService;

    @MessageMapping("/participants/{roomId}")
    @SendTo("/topic/participants/{roomId}")
    public List<RoomParticipantResponse> updateRoomParticipantList(@DestinationVariable Long roomId) {
        return roomService.getParticipantList(roomId);
    }

}
