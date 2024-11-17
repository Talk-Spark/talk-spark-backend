package mutsa.yewon.talksparkbe.domain.guestBook.dto.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestBookRoomListDTO {

    private Long roomId;
    private String roomName;
    private LocalDateTime roomDateTime;
    private Long roomPeopleCount;
    private boolean isGuestBookFavorited;
    private String preViewContent;
}
