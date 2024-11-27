package mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(title = "방 방명록 목록 조회")
public class GuestBookListRequestDTO {

    @Schema(description = "방 식별자", example = "1")
    @NotNull(message = "roomId는 반드시 필요합니다.")
    private Long roomId;

    @Schema(description = "사용자의 식별자", example = "1")
    @NotNull(message = "sparkUserId는 반드시 필요합니다.")
    private Long sparkUserId;
}
