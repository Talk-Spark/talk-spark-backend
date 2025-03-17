package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.Getter;
import mutsa.yewon.talksparkbe.domain.game.entity.QuestionTip;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardBlanksDto;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardQuestion;
import mutsa.yewon.talksparkbe.domain.game.service.dto.SwitchSubject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GameQuestionManager {

    private List<CardQuestion> questions;

    private int currentQuestionIndex = 0;

    private Long currentPlayerId;

    private Map<Long, CardBlanksDto> blanks;

    private Map<Long, List<String>> roomQuestionTips = new HashMap<>();

    public GameQuestionManager(List<CardQuestion> questions, Map<Long, CardBlanksDto> blanks) {
        this.questions = questions;
        this.blanks = blanks;
        currentPlayerId = questions.get(0).getCardOwnerId();
    }


    public CardQuestion getCurrentQuestion() {

        return questions.get(currentQuestionIndex);
    }

    public boolean checkAnswer(String answer) {
        return getCurrentQuestion().getCorrectAnswer().equals(answer);
    }

    public CardBlanksDto getQuestionBlanks(){
        return blanks.get(currentPlayerId);
    }

    public void loadNextQuestion() {
        currentQuestionIndex++;
        resetQuestionTips();
    }


    public SwitchSubject isSwitchingSubject() {

        if(currentQuestionIndex == questions.size() - 1){
            return SwitchSubject.END;
        }

        Long nextQuestionOwnerId = questions.get(currentQuestionIndex + 1).getCardOwnerId();

        if (!nextQuestionOwnerId.equals(currentPlayerId)) {
            return SwitchSubject.TRUE;
        }
        else {
            return SwitchSubject.FALSE;
        }
    }

    public List<String> getQuestionTips(Long roomId, String field) {
        return roomQuestionTips.computeIfAbsent(roomId, k -> {
            List<String> randomQuestions = QuestionTip.valueOf(field).getRandomQuestions();
            return new ArrayList<>(randomQuestions); // 저장 후 반환
        });
    }

    public void resetQuestionTips() {
        roomQuestionTips.clear();
    }

    public void switchCurrentPlayerId(){
        currentPlayerId = questions.get(currentQuestionIndex + 1).getCardOwnerId();
    }
}
