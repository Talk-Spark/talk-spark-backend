package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.service.dto.*;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Getter
public class GameState {

    private final List<Long> participantIds = new ArrayList<>();
    private final List<Card> cards;
    private final List<CardQuestion> questions;
    private final Integer roomPeople;
    private Long currentSubjectId;
    private final Map<CardQuestion, Integer> answerNums = new HashMap<>(); // 각 문제마다 답 제출한 사람 수
    private final List<CorrectAnswerDto> currentQuestionCorrect = new ArrayList<>(); // 현재 문제 정답 여부. 유저아이디 : 맞춤
    private final Map<Long, Integer> scores = new HashMap<>(); // 유저아이디 : 점수
    private final List<CardBlanksDto> cardBlanksDtos = new ArrayList<>();

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
        return questions.get(0);
    }

    public boolean hasNextQuestion() {
        return !questions.isEmpty();
    }

    public void recordScore(Long sparkUserId, String answer) {
        CardQuestion currentQuestion = questions.get(0);
        if (currentQuestion.getCorrectAnswer().equals(answer)) {

            scores.put(sparkUserId, scores.getOrDefault(sparkUserId, 0) + 1);

            Card card = cards.stream()
                    .filter(c -> c.getSparkUser().getId().equals(sparkUserId)).findFirst()
                    .orElseThrow();

            currentQuestionCorrect.add(CorrectAnswerDto.builder()
                    .sparkUserId(sparkUserId)
                    .isCorrect(Boolean.TRUE)
                    .name(card.getName())
                    .color(card.getCardThema())
                    .build());

//            scores.put(sparkUserId, scores.getOrDefault(sparkUserId, 0) + 1);
//            currentQuestionCorrect.add(CorrectAnswerDto.builder().
//                    sparkUserId(sparkUserId).isCorrect(Boolean.TRUE).build());
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

            currentSubjectId = questions.get(1).getCardOwnerId();
            return SwitchSubject.TRUE;
        } else return SwitchSubject.FALSE;
    }

    public Card getCurrentCard() {
        Long cardId = getCurrentQuestion().getCardId();
        return cards.stream().filter(c -> c.getId().equals(cardId)).findFirst().orElse(null);
    }

    public CardBlanksDto getCurrentCardBlanks() {
        Long sparkUserId = getCurrentQuestion().getCardOwnerId();
        return cardBlanksDtos.stream().filter(cbd -> cbd.getSparkUserId().equals(sparkUserId)).findFirst().orElse(null);
    }
}
