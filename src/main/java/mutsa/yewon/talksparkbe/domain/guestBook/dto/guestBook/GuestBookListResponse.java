package mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Schema(title = "방 방명록 목록 반환")
public class GuestBookListResponse {

    @Schema(description = "방 식별자", example = "1")
    private Long roomId;

    @Schema(description = "방 이름", example = "멋사멋사")
    private String roomName;

    @Schema(description = "방 생성날짜", example = "2024-11-27 02:50:48.000000")
    private LocalDateTime roomDateTime;

    @Schema(description = "방 방명록 즐겨찾기", example = "false")
    private boolean isGuestBookFavorited;

    @Schema(description = "방 방명록 목록")
    private List<GuestBookListDTO> guestBookData;
}
