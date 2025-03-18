package mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;

@Data
@Builder(access = AccessLevel.PRIVATE)
@Schema
public class RoomCreateResponse {

    @Schema(description = "생성된 방 ID", example = "1")
    private Long roomId;
    @Schema(description = "생성된 방 이름", example = "멋사")
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
