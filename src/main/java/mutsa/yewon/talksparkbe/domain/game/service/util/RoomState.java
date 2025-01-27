package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.Getter;
import lombok.Setter;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardQuestion;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Component
public class RoomState {

    private final Map<Long, List<RoomParticipate>> roomParticipants = new HashMap<>();

    public boolean existRoomId(long roomId) {
        return roomParticipants.containsKey(roomId);
    }

    public void addParticipant(Long roomId, RoomParticipate participant) {
        roomParticipants.computeIfAbsent(roomId, k -> new ArrayList<>()).add(participant);
    }

    public List<RoomParticipate> getParticipantsByRoomId(Long roomId) {
        return roomParticipants.getOrDefault(roomId, new ArrayList<>());
    }

    public void removeParticipant(Long roomId, RoomParticipate participant) {
        List<RoomParticipate> participants = roomParticipants.get(roomId);
        if (participants != null) {
            participants.remove(participant);
        }
    }

    public void clearParticipantsByRoomId(Long roomId) {
        roomParticipants.remove(roomId);
    }
}
