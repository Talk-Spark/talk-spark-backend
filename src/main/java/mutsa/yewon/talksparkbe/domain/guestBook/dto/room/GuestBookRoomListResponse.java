package mutsa.yewon.talksparkbe.domain.guestBook.dto.room;

import lombok.*;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class GuestBookRoomListResponse {
    private Long roomGuestBookCount;
    private List<GuestBookRoomListDTO> roomGuestBook;
}
