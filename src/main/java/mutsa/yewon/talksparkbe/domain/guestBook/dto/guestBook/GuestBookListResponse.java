package mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GuestBookListResponse {

    private Long roomId;
    private String roomName;
    private LocalDateTime roomDateTime;
//    private boolean guestBookFavorited;
    private List<GuestBookListDTO> guestBookData;
}
