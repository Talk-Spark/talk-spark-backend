package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mutsa.yewon.talksparkbe.domain.game.controller.dto.GameCardInfo;
import mutsa.yewon.talksparkbe.domain.game.service.dto.AnswerDto;
import mutsa.yewon.talksparkbe.domain.game.service.dto.PlayerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class GamePlayerManager {

    private Map<Long, PlayerInfo> playerInfo;

    private List<AnswerDto> answers = new ArrayList<>();

    private Map<Long, GameCardInfo> playerCards;

    public GamePlayerManager(Map<Long, PlayerInfo> playerInfo, Map<Long, GameCardInfo> playerCards) {
        this.playerInfo = playerInfo;
        this.playerCards = playerCards;
    }

    public void markAnsweredPlayer(Long playerId, boolean isCorrect) {

        AnswerDto answerDto = AnswerDto.builder()
                .sparkUserId(playerId)
                .name(playerInfo.get(playerId).getPlayerName())
                .isCorrect(isCorrect)
                .color(playerInfo.get(playerId).getCardThema())
                .build();

        answers.add(answerDto);
    }

    public boolean allAnswered() {

        return answers.size() == playerInfo.size();

    }

    public GameCardInfo getCurrentCard(Long currentPlayerId) {

        return playerCards.get(currentPlayerId);
    }

    public void clearAnswers() {
        answers.clear();
    }

    public List<GameCardInfo> getAllPlayerCards() {
        return new ArrayList<>(playerCards.values());
    }
}
