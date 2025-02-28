package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.Getter;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardBlanksDto;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardQuestion;
import mutsa.yewon.talksparkbe.domain.game.service.dto.SwitchSubject;

import java.util.List;
import java.util.Map;

@Getter
public class GameQuestionManager {

    private List<CardQuestion> questions;

    private int currentQuestionIndex = 0;

    private Long currentPlayerId;

    private Map<Long, CardBlanksDto> blanks;

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
    }


    public SwitchSubject isSwitchingSubject() {

        if(currentQuestionIndex == questions.size() - 1){
            return SwitchSubject.END;
        }

        Long nextQuestionOwnerId = questions.get(currentQuestionIndex + 1).getCardOwnerId();

        if (!nextQuestionOwnerId.equals(currentPlayerId)) {
            currentPlayerId = nextQuestionOwnerId;
            return SwitchSubject.TRUE;
        }
        else {
            return SwitchSubject.FALSE;
        }

    }
}
