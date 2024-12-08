package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.Getter;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardQuestion;
import mutsa.yewon.talksparkbe.domain.game.service.dto.SwitchSubject;
import mutsa.yewon.talksparkbe.domain.game.service.dto.UserCardQuestions;

import java.util.*;

@Getter
public class GameState {

    private final List<Long> participantIds = new ArrayList<>();

    private final List<Card> cards;
    private final List<CardQuestion> questions;

    private final Integer roomPeople;

    private Long currentSubjectId;
    private final Map<CardQuestion, Integer> answerNums = new HashMap<>(); // 각 문제마다 답 제출한 사람 수
    private final Map<Long, Boolean> currentQuestionCorrect = new HashMap<>(); // 현재 문제 정답 여부. 유저아이디 : 맞춤

    private final Map<Long, Integer> scores = new HashMap<>(); // 유저아이디 : 점수

    public GameState(List<Card> cards, List<UserCardQuestions> userCardQuestions, Integer roomPeople) {
        this.cards = cards;
        this.questions = new LinkedList<>();
        this.roomPeople = roomPeople;
        cards.forEach(c -> this.participantIds.add(c.getSparkUser().getId()));
        userCardQuestions.forEach(card -> questions.addAll(card.getQuestions()));
        questions.forEach(cq -> answerNums.put(cq, 0));
        this.currentSubjectId = userCardQuestions.get(0).getSparkUserId();
    }

    public CardQuestion getCurrentQuestion() {
        currentSubjectId = questions.get(0).getCardOwnerId();
        return questions.get(0);
    }

    public boolean hasNextQuestion() {
        return !questions.isEmpty();
    }

    public void recordScore(Long sparkUserId, String answer) {
        CardQuestion currentQuestion = questions.get(0);
        if (currentQuestion.getCorrectAnswer().equals(answer)) {
            scores.put(sparkUserId, scores.getOrDefault(sparkUserId, 0) + 1);
            currentQuestionCorrect.put(sparkUserId, Boolean.TRUE);
        }
        answerNums.put(currentQuestion, answerNums.get(currentQuestion) + 1);
    }

    public Integer getCurrentQuestionAnswerNum() {
        CardQuestion currentQuestion = questions.get(0);
        return answerNums.get(currentQuestion);
    }

    public void loadNextQuestion() {
        if (questions.isEmpty()) return;
        questions.remove(0);
        currentQuestionCorrect.clear();
    }

    public SwitchSubject isSwitchingSubject() {
        if (questions.size() < 2) return SwitchSubject.END;
        else if (!questions.get(1).getCardOwnerId().equals(currentSubjectId)) {
            return SwitchSubject.TRUE;
        } else return SwitchSubject.FALSE;
    }

    public Card getCurrentCard() {
        Long cardId = getCurrentQuestion().getCardId();

        for (Card c : cards) {
            if (c.getId().equals(cardId)) return c;
        }
        return null;
    }

}
