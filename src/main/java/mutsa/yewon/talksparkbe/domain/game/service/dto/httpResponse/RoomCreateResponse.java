package mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;

@Data
@Builder(access = AccessLevel.PRIVATE)
public class RoomCreateResponse {

    private Long roomId;
    private String roomName;

    public static RoomCreateResponse of(Long roomId, String roomName) {
        return RoomCreateResponse.builder()
                .roomId(roomId)
                .roomName(roomName)
                .build();
    }

    public static RoomCreateResponse from(Room room) {
        return RoomCreateResponse.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .build();
    }

}
