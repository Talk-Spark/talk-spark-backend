package mutsa.yewon.talksparkbe.domain.game.controller.request;

import lombok.Data;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;

@Data
public class RoomCreateRequest {

    private String roomName;

    private int maxPeople;

    private int difficulty;

    private Long hostId; // TODO: 임시용. 삭제 혹은 변경 예정

    public Room toRoomEntity() {
        return Room.builder()
                .roomName(this.roomName)
                .maxPeople(this.maxPeople)
                .difficulty(this.difficulty)
                .build();
    }

}
