package mutsa.yewon.talksparkbe.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice

public class TalkSparkExceptionHandler {

    @ExceptionHandler(CustomTalkSparkException.class)
    protected ResponseEntity<ErrorResponseEntity> handleCustomTalkSparkException(CustomTalkSparkException ex) {
        return ErrorResponseEntity.errorResponseEntity(ex.getErrorCode());
    }
}
