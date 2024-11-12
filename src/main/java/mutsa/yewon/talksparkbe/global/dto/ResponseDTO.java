package mutsa.yewon.talksparkbe.global.dto;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public class ResponseDTO<D> {

    private final int status;
    private final String message;
    private final D data;

    // OK (200)
    public static <D> ResponseDTO<D> ok(String message) {
        return new ResponseDTO<>(HttpStatus.OK.value(), message, null);
    }
    public static <D> ResponseDTO<D> ok(String message, D data) {
        return new ResponseDTO<>(HttpStatus.OK.value(), message, data);
    }

    // Created (201)
    public static <D> ResponseDTO<D> created(String message) {
        return new ResponseDTO<>(HttpStatus.CREATED.value(), message, null);
    }

    // Bad Request (400)
    public static <D> ResponseDTO<D> badRequest(String message) {
        return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    // Unauthorized (401)
    public static <D> ResponseDTO<D> unauthorized(String message) {
        return new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), message, null);
    }

    // Forbidden (403)
    public static <D> ResponseDTO<D> forbidden(String message) {
        return new ResponseDTO<>(HttpStatus.FORBIDDEN.value(), message, null);
    }

    // Not Found (404)
    public static <D> ResponseDTO<D> notFound(String message) {
        return new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), message, null);
    }

    // Conflict (409)
    public static <D> ResponseDTO<D> conflict(String message) {
        return new ResponseDTO<>(HttpStatus.CONFLICT.value(), message, null);
    }

    // Internal Server Error (500)
    public static <D> ResponseDTO<D> internalServerError(String message) {
        return new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
    }
}
