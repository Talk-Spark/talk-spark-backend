package mutsa.yewon.talksparkbe.domain.game.service.dto;

import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Data
public class CardQuestion {
    private Long cardId;
    private Long cardOwnerId;
    private String fieldName;  // 빈칸의 필드 이름 (ex: "name", "age")
    private String correctAnswer;  // 정답
    private Set<String> options; // 보기 리스트

    public CardQuestion(Long cardId, Long sparkUserId, String fieldName, String correctAnswer, Set<String> options) {
        this.cardId = cardId;
        this.cardOwnerId = sparkUserId;
        this.fieldName = fieldName;
        this.correctAnswer = correctAnswer;
        this.options = options;
    }
}
