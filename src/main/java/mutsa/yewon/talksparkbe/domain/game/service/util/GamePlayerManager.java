package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.Getter;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CorrectAnswerDto;
import mutsa.yewon.talksparkbe.domain.game.service.dto.PlayerInfo;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class GamePlayerManager {


    private Map<Long, PlayerInfo> playerInfo;

    private List<CorrectAnswerDto> currentQuestionCorrect = new ArrayList<>();

    private Map<Long, Card> playerCards;

    public GamePlayerManager(Map<Long, PlayerInfo> playerInfo, Map<Long, Card> playerCards) {
        this.playerInfo = playerInfo;
        this.playerCards = playerCards;
    }

    public void markAnsweredPlayer(Long playerId, boolean isCorrect) {

        CorrectAnswerDto correctAnswerDto = CorrectAnswerDto.builder()
                .sparkUserId(playerId)
                .name(playerInfo.get(playerId).getPlayerName())
                .isCorrect(isCorrect)
                .color(playerInfo.get(playerId).getCardThema())
                .build();

        currentQuestionCorrect.add(correctAnswerDto);
    }

    public boolean allAnswered() {

        return currentQuestionCorrect.size() == playerInfo.size();

    }

    public Card getCurrentCard(Long currentPlayerId) {

        return playerCards.get(currentPlayerId);
    }

    public void clearAnswers() {
        currentQuestionCorrect.clear();
    }

    public List<Card> getAllPlayerCards() {
        return new ArrayList<>(playerCards.values());
    }

}
