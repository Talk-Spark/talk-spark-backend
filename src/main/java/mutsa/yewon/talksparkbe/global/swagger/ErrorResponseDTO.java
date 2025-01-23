package mutsa.yewon.talksparkbe.global.swagger;

import lombok.Builder;
import lombok.Getter;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;

@Getter
@Builder
public class ErrorResponseDTO {
    private int status;
    private String message;
    private String description;

    public static ErrorResponseDTO from(ErrorCode errorCode) {
        return ErrorResponseDTO.builder()
                .status(errorCode.getHttpStatus().value())
                .message(errorCode.name())
                .description(errorCode.getMessage())
                .build();
    }
}
