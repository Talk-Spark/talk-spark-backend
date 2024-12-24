package mutsa.yewon.talksparkbe.domain.guestBook.dto.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
@Schema(title = "방명록 방 목록 반환 값")
public class GuestBookRoomListResponse {
    @Schema(description = "방명록 방 수", example = "5")
    private Long guestBookRoomCount;

    @Schema(description = "방명록 방 목록 데이터")
    private List<GuestBookRoomListDTO> guestBookRooms;
}
