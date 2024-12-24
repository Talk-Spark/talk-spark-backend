package mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "방명록 작성 request 데이터")
public class GuestBookPostRequestDTO {

    @Schema(description = "방 식별자", example = "1")
    @NotNull(message = "roomId는 반드시 필요합니다.")
    private Long roomId;

    @Schema(description = "사용자의 식별자", example = "1")
    @NotNull(message = "sparkUserId는 반드시 필요합니다.")
    private Long sparkUserId;

    @Schema(description = "작성 내용", example = "머찌다 우리팀")
    private String content;

    @Schema(description = "방명록 생성날짜", example = "2024-11-27 02:50:48.000000")
    private LocalDateTime guestBookDateTime;

    public GuestBookPostRequestDTO(Long sparkUserId, Long roomId,GuestBookContent content) {
        this.roomId = roomId;
        this.sparkUserId = sparkUserId;
        this.content = content.getContent();
    }
}
