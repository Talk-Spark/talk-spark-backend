package mutsa.yewon.talksparkbe.domain.game.controller.dto;

import lombok.Builder;
import lombok.Getter;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardResponseCustomDTO;

import java.util.List;
import java.util.Map;

@Getter
public class GameScoreDto {

    private final Map<Long, Integer> scores;

    private final List<CardResponseCustomDTO> playerCards;

    private GameScoreDto(Map<Long, Integer> scores, List<CardResponseCustomDTO> playerCards) {
        this.scores = scores;
        this.playerCards = playerCards;
    }

    public static GameScoreDto from(Map<Long, Integer> scores, List<CardResponseCustomDTO> playerCards) {
        return new GameScoreDto(scores, playerCards);
    }
}
