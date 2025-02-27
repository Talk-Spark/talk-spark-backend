package mutsa.yewon.talksparkbe.domain.game.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EndGameDto {

    @Schema(description = "방 식별자", example = "1")
    private Long roomId;

    @Schema(description = "게임 참가자 식별자", example = "2")
    private Long playerId;

    public EndGameDto(Long roomId, Long playerId) {
        this.roomId = roomId;
        this.playerId = playerId;
    }
}
