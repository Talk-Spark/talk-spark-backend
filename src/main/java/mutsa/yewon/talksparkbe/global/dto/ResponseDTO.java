package mutsa.yewon.talksparkbe.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "API 응답 DTO", oneOf = { ResponseDTO.class })
public class ResponseDTO<D> {

    @Schema(description = "응답 상태 코드", example = "200")
    private final int status;

    @Schema(description = "응답 메세지", example = "api 응답 메세지")
    private final String message;

    @Schema(description = "응답 데이터")
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

    public static <D> ResponseDTO<D> created(String message, D data) {
        return new ResponseDTO<>(HttpStatus.CREATED.value(), message, data);
    }

}
