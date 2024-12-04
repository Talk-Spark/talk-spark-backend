package mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse;

import lombok.Builder;
import lombok.Data;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;

@Data
public class RoomListResponse {

    private Long roomId;
    private String roomName;
    private String hostName;
    private int currentPeople;
    private int maxPeople;

    @Builder
    private RoomListResponse(Long roomId, String roomName, int maxPeople) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.maxPeople = maxPeople;
    }

    public static RoomListResponse from(Room room) {
        return RoomListResponse.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .maxPeople(room.getMaxPeople())
                .build();
    }

}
