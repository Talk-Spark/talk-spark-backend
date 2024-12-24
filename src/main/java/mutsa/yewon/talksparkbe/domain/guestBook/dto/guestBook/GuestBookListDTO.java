package mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import mutsa.yewon.talksparkbe.domain.card.entity.CardThema;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Data
@Builder
@Schema(title = "방명록 정보 조회")
public class GuestBookListDTO {

    @Schema(description = "방명록 식별자", example = "1")
    private Long guestBookId;

    @Schema(description = "작성자 확인(true일 때, 작성자)", example = "true")
    private boolean isOwnerGuestBook;

    @Schema(description = "작성자 이름", example = "김멋사")
    private String sparkUserName;

    @Schema(description = "작성자 테마", example = "YELLOW")
    private CardThema cardThema;

    @Schema(description = "방명록 내용", example = "오늘 정말 재밌었어요!!")
    private String guestBookContent;

    @Schema(description = "방명록 생성날짜", example = "2024-11-27 02:50:48.000000")
    private LocalDateTime guestBookDateTime;
}
