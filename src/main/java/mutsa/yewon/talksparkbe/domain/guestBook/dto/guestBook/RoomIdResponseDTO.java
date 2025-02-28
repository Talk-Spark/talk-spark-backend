package mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "방 ID response 데이터")
public class RoomIdResponseDTO {

    @Schema(description = "방 식별자", example = "1")
    private Long roomId;
}
