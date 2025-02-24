package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.service.dto.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Getter
@Log4j2
public class GameStateManager {
 // 현재 문제의 정답 여부, 답을 한 인원 수

    private final GameQuestionManager questionManager;

    private final GameScoreManager scoreManager;

    private final GamePlayerManager playerManager;

    public GameStateManager(List<Card> cards, List<UserCardQuestions> userCardQuestions) {

        Map<Long, PlayerInfo> playerInfos = createPlayerInfos(cards);

        Map<Long, Card> playerCards = collectPlayerCards(cards);

        List<CardQuestion> cardQuestions = extractCardQuestions(userCardQuestions);

        Map<Long, CardBlanksDto> blanks = createCardBlanks(userCardQuestions);

        Map<Long, Integer> initedScoreBoard = initScoreBoard(cards);

        this.questionManager = new GameQuestionManager(cardQuestions, blanks);

        this.scoreManager = new GameScoreManager(initedScoreBoard);

        this.playerManager = new GamePlayerManager(playerInfos, playerCards);
    }

    private Map<Long, Integer> initScoreBoard(List<Card> cards) {
        return cards.stream()
                .collect(toMap(
                        card -> card.getSparkUser().getId(),
                        card -> 0
                ));
    }

    public CardQuestion getCurrentQuestion() {
        return questionManager.getCurrentQuestion();
    }

    public void recordScore(Long sparkUserId, String answer) {

        if (questionManager.checkAnswer(answer)) { // 정답을 맞춘 경우

            scoreManager.updateScore(sparkUserId); // 점수 추가

            playerManager.markAnsweredPlayer(sparkUserId, Boolean.TRUE); // 정답 객체 추가
        }
        else{
            playerManager.markAnsweredPlayer(sparkUserId, Boolean.FALSE);
        }
    }

    public boolean allAnswered() {
        return playerManager.allAnswered();
    }

    public void prepareNextQuestion() {

        questionManager.loadNextQuestion();
        playerManager.clearAnswers();
    }

    public SwitchSubject isSwitchingSubject() {

        return questionManager.isSwitchingSubject();
    }

    public Card getCurrentCard() {
        return playerManager.getCurrentCard(questionManager.getCurrentPlayerId());
    }

    public CardBlanksDto getCurrentCardBlanks() {
        return questionManager.getQuestionBlanks();
    }

    public List<CorrectAnswerDto> getScoreBoard() {

        return playerManager.getCurrentQuestionCorrect();
    }

    public Map<Long, Integer> getScores() {
        return scoreManager.getScores();
    }

    public List<CardResponseCustomDTO> getAllPlayerCards() {
        return playerManager.getAllPlayerCards().stream()
                .map(CardResponseCustomDTO::fromCard).toList();
    }

    public List<Long> getPlayerIds() {
        return new ArrayList<>(playerManager.getPlayerInfo().keySet());
    }

    public List<Long> getCardIdsToStore(Long sparkUserId) {

        return playerManager.getPlayerCards().keySet().stream()
                .filter(card -> !card.equals(sparkUserId))
                .toList();

    }

    public List<CardBlanksDto> getBlanks() {
        return questionManager.getBlanks().values().stream().toList();
    }


    private Map<Long, PlayerInfo> createPlayerInfos(List<Card> cards){
        return cards.stream()
                .collect(toMap(
                        card -> card.getSparkUser().getId(),
                        card -> new PlayerInfo(card.getSparkUser().getId(), card.getName(), card.getCardThema())
                ));
    }

    private Map<Long, Card> collectPlayerCards(List<Card> cards) {
        return cards.stream()
                .collect(toMap(
                        card -> card.getSparkUser().getId(),
                        card -> card
                ));
    }

    private List<CardQuestion> extractCardQuestions(List<UserCardQuestions> userCardQuestions) {
        return userCardQuestions.stream()
                .flatMap(q -> q.getQuestions().stream())
                .collect(Collectors.toList());
    }

    private Map<Long, CardBlanksDto> createCardBlanks(List<UserCardQuestions> userCardQuestions) {
        return userCardQuestions.stream()
                .collect(Collectors.toMap(
                        UserCardQuestions::getSparkUserId,
                        q -> CardBlanksDto.of(q.getSparkUserId(),
                                q.getQuestions().stream().map(CardQuestion::getFieldName).toList())
                ));
    }
}
