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
    USER_NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    MUST_MAKE_CARD_FIRST(HttpStatus.NOT_FOUND, "현재 사용자님의 명함이 아직 만들어지지 않았습니다."),
    CARD_NOT_EXIST(HttpStatus.NOT_FOUND,"해당하는 명함이 존재하지 않습니다."),
    USER_NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "JWT 토큰 인증이 필요한 API입니다."),

    LOCK_TIMEOUT(HttpStatus.SERVICE_UNAVAILABLE, "락 획득 대기 시간이 초과되었습니다. 잠시 후 다시 시도해주세요."),
    ROOM_FULL(HttpStatus.FORBIDDEN, "방이 꽉 찼습니다."),
    ROOM_JOIN_INTERRUPTED(HttpStatus.SERVICE_UNAVAILABLE, "방 입장 처리가 강제로 중단되었습니다. 잠시 후 다시 시도해주세요."),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "방을 찾지 못했습니다."),

    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 형식입니다."),
    GUESTBOOK_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "방명록 방을 찾지 못했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
