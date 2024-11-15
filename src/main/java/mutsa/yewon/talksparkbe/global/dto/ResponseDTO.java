package mutsa.yewon.talksparkbe.global.dto;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
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

}
