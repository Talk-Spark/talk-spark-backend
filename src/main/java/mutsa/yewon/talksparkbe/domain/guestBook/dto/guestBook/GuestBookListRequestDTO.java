package mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GuestBookListRequestDTO {
    @NotNull(message = "roomId는 반드시 필요합니다.")
    private Long roomId;

    @NotNull(message = "sparkUserId는 반드시 필요합니다.")
    private Long sparkUserId;
}
