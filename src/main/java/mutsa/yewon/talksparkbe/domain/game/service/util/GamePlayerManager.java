package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.Getter;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.service.dto.AnswerDto;
import mutsa.yewon.talksparkbe.domain.game.service.dto.PlayerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class GamePlayerManager {

    private Map<Long, PlayerInfo> playerInfo;

    private List<AnswerDto> answers = new ArrayList<>();

    private Map<Long, Card> playerCards;

    public GamePlayerManager(Map<Long, PlayerInfo> playerInfo, Map<Long, Card> playerCards) {
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

    public Card getCurrentCard(Long currentPlayerId) {

        return playerCards.get(currentPlayerId);
    }

    public void clearAnswers() {
        answers.clear();
    }

    public List<Card> getAllPlayerCards() {
        return new ArrayList<>(playerCards.values());
    }
}
