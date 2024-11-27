package mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "방명록 작성 데이터")
public class GuestBookContent {

    @Schema(description = "사용자의 식별자", example = "1")
    private Long sparkUserId;

    @Schema(description = "작성 내용", example = "머찌다 우리팀")
    private String content;
}