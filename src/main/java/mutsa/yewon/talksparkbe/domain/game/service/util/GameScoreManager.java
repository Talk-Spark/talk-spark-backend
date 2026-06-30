package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class GameScoreManager {

    private Map<Long, Integer> scores;

    public void updateScore(Long playerId){
        scores.put(playerId, scores.getOrDefault(playerId, 0) + 1);
    }

    public GameScoreManager(Map<Long, Integer> scores) {
        this.scores = scores;
    }
}
