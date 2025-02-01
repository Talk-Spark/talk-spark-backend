package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.Getter;
import lombok.Setter;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardQuestion;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.springframework.stereotype.Component;

import java.util.*;

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

    public void removeParticipant(Long roomId, SparkUser sparkUser) {
        List<RoomParticipate> participants = roomParticipants.get(roomId);
        if (participants != null) {
            Iterator<RoomParticipate> iterator = participants.iterator();
            while (iterator.hasNext()) {
                RoomParticipate p = iterator.next();
                if (p.getSparkUser().getId().equals(sparkUser.getId())) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    public Long findUserIdByRoomIdAndParticipant(Long roomId, RoomParticipate participant) {
        List<RoomParticipate> participants = roomParticipants.get(roomId);
        if (participants != null) {
            for (RoomParticipate p : participants) {
                if (p.equals(participant)) {
                    return p.getSparkUser().getId();
                }
            }
        }
        return null;
    }

    public void clearParticipantsByRoomId(Long roomId) {
        roomParticipants.remove(roomId);
    }
}
