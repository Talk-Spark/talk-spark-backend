package mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Data
@Builder
public class GuestBookListDTO {
    private Long guestBookId;
    private boolean isOwnerGuestBook;
    private String sparkUserName;
    private String guestBookContent;
    private LocalDateTime guestBookDateTime;
}
