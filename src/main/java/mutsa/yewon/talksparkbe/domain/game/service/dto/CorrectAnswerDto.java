package mutsa.yewon.talksparkbe.domain.game.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CorrectAnswerDto {

    private Long sparkUserId;
    private String name;
    private boolean isCorrect;

}
