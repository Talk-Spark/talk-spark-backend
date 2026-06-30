package mutsa.yewon.talksparkbe.domain.game.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mutsa.yewon.talksparkbe.domain.card.entity.CardThema;

@Getter
@Setter
@NoArgsConstructor
public class PlayerInfo {

    private Long playerId;

    private String playerName;

    private CardThema cardThema;

    public PlayerInfo(Long playerId, String playerName, CardThema cardThema) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.cardThema = cardThema;
    }
}
