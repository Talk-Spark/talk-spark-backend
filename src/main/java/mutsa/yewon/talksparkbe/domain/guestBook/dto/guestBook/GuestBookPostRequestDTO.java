package mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestBookPostRequestDTO {

    @NotNull(message = "roomId는 반드시 필요합니다.")
    private Long roomId;

    @NotNull(message = "sparkUserId는 반드시 필요합니다.")
    private Long sparkUserId;
    private String content;
    private LocalDateTime createdAt;

    public GuestBookPostRequestDTO(Long roomId,GuestBookContent content) {
        this.roomId = roomId;
        this.sparkUserId = content.getSparkUserId();
        this.content = content.getContent();
    }
}
