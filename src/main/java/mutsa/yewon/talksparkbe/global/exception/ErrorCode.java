package mutsa.yewon.talksparkbe.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰 입니다."),
    INVALID_JWT_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰 입니다."),
    JWT_MALFORMED(HttpStatus.BAD_REQUEST, "훼손된 토큰 입니다."),
    TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "JWT 토큰 인증이 필요한 API입니다."),
    USER_NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");

    private final HttpStatus httpStatus;

    private final String message;

}
