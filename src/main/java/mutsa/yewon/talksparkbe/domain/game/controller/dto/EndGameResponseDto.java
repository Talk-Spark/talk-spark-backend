package mutsa.yewon.talksparkbe.domain.game.controller.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardResponseCustomDTO;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class EndGameResponseDto {

    @Schema(description = "최종 점수판", example = "{1 : 10, 2 : 5}")
    private Map<Long, Integer> scores;

    @ArraySchema(arraySchema = @Schema(description = "게임에 참여한 모든 참여자 명함들"))
    private List<CardResponseCustomDTO> allPlayedCards;

    public static EndGameResponseDto of(Map<Long, Integer> scores,List<CardResponseCustomDTO> allPlayedCards ){
        return new EndGameResponseDto(scores, allPlayedCards);
    }
}
