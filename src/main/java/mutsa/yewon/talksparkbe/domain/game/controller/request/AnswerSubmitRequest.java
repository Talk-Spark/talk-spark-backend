package mutsa.yewon.talksparkbe.domain.game.controller.request;

import lombok.Data;

@Data
public class AnswerSubmitRequest {
    private Long roomId;
    private Long sparkUserId;
    private String answer;
}
