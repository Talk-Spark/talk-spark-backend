package mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse;

import lombok.Builder;
import lombok.Data;

@Data
public class RoomDetailsResponse {

    private Long roomId;
    private String roomName;
    private int difficulty;
    private int maxPeople;

    @Builder
    private RoomDetailsResponse(Long roomId, String roomName, int difficulty, int maxPeople) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.difficulty = difficulty;
        this.maxPeople = maxPeople;
    }

}
