package mutsa.yewon.talksparkbe.domain.game.controller.request;

import lombok.Getter;

@Getter
public class AnswerSubmitRequest {
    private Long roomId;
    private Long sparkUserId;
    private String answer;
}
