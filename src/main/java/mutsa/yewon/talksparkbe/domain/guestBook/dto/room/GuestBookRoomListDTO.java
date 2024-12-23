package mutsa.yewon.talksparkbe.domain.guestBook.dto.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "방명록 방 목록 내용")
public class GuestBookRoomListDTO {

    @Schema(description = "방 식별자", example = "1")
    private Long roomId;

    @Schema(description = "방 이름", example = "멋사멋사팀")
    private String roomName;

    @Schema(description = "방 생성날짜", example = "2024-11-27 02:50:48.000000")
    private LocalDateTime roomDateTime;

    @Schema(description = "방 참여자", example = "3")
    private Long roomPeopleCount;

    @Schema(description = "즐겨찾기 여부", example = "true")
    private boolean isGuestBookFavorited;

    @Schema(description = "마지막 방명록 미리보기", example = "재밌었습니다~!")
    private String preViewContent;
}
