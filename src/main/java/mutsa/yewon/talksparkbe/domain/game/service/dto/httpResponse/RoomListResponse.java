package mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse;

import lombok.Builder;
import lombok.Data;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;

@Data
@Builder
public class RoomListResponse {

    private Long roomId;
    private String roomName;
    private String hostName;
    private int currentPeople;
    private int maxPeople;

    public static RoomListResponse from(Room room) {
        return RoomListResponse.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .maxPeople(room.getMaxPeople())
                .currentPeople(room.getRoomParticipates().size())
                .hostName(room.getRoomParticipates().stream()
                        .filter(RoomParticipate::isOwner)
                        .findFirst()
                        .map(rp -> rp.getSparkUser().getName())
                        .orElse("알수없음"))
                .build();
    }

}
