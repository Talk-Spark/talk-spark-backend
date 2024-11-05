package mutsa.yewon.talksparkbe.domain.game.service.response;

import lombok.Builder;
import lombok.Data;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;

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
                .build();
    }

}
