package mutsa.yewon.talksparkbe.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomTalkSparkException extends RuntimeException {
    private final ErrorCode errorCode;
}
