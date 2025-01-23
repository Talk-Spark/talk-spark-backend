package mutsa.yewon.talksparkbe.domain.game.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
public class UserCardQuestions {
    private Long sparkUserId; // 참가자 ID
    private List<CardQuestion> questions; // 해당 참가자의 질문 리스트
}
