package mutsa.yewon.talksparkbe.global.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ErrorResponseEntity {
    private int status;

    private String message;

    private String details;

    public static ResponseEntity<ErrorResponseEntity> errorResponseEntity(ErrorCode errorCode) {

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(errorCode.getHttpStatus().value())
                        .message(errorCode.getMessage())
                        .details(errorCode.name()).build());
    }
}
