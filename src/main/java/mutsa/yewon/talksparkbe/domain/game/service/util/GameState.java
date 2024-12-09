package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.Getter;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardQuestion;
import mutsa.yewon.talksparkbe.domain.game.service.dto.UserCardQuestions;

import java.util.*;

@Getter
public class GameState {
    private final Queue<CardQuestion> questions;
    private final Map<Long, Integer> scores = new HashMap<>();

    private final Map<CardQuestion, Integer> answerNums = new HashMap<>();
    private final Integer roomPeople;
    private final List<Card> cards;

    public GameState(List<Card> cards, List<UserCardQuestions> cardQuestions, Integer roomPeople) {
        this.cards = cards;
        this.questions = new LinkedList<>();
        this.roomPeople = roomPeople;
        cardQuestions.forEach(card -> questions.addAll(card.getQuestions()));
        questions.forEach(cq -> answerNums.put(cq, 0));
    }

    public CardQuestion getNextQuestion() {
        return questions.poll();
    }

    public boolean hasNextQuestion() {
        return !questions.isEmpty();
    }

    public void recordAnswer(Long sparkUserId, String answer) {
        // 정답 검증 및 점수 갱신
        CardQuestion current = questions.peek();
        if (current != null && current.getCorrectAnswer().equals(answer)) {
            scores.put(sparkUserId, scores.getOrDefault(sparkUserId, 0) + 1);
        }
        answerNums.put(current, answerNums.get(current) + 1);
    }

    public int getCurAnswerNum() {
        CardQuestion current = questions.peek();
        return answerNums.get(current);
    }

    public Card getCurrentCard() {
        CardQuestion current = questions.peek();
        Long cardId = current.getCardId();

        for (Card c : cards) {
            if (c.getId().equals(cardId)) return c;
        }
        return null;
    }
}
