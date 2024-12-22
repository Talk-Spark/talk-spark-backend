package mutsa.yewon.talksparkbe.domain.guestBook;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mutsa.yewon.talksparkbe.domain.card.dto.CardCreateDTO;
import mutsa.yewon.talksparkbe.domain.card.dto.CardResponseDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookContent;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookListResponse;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import mutsa.yewon.talksparkbe.global.swagger.ApiErrorCodes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

//TODO: 방명록 API 설정
@Tag(name = "방명록 API", description = "방명록에 관한 생성/조회/수정/삭제를 수행하는 API")
public interface GuestBookControllerDocs {
    @Operation(summary = "방명록 생성", description = "각 방에서 사용자의 방명록을 생성할 때 사용하는 API")
    @ApiErrorCodes({ErrorCode.USER_NOT_EXIST, ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED, ErrorCode.INVALID_JWT_TOKEN})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "방명록 생성 성공")
    })
    ResponseEntity<?> postGuestBook(@PathVariable("roomId") Long roomId,
                                      @Valid @RequestBody GuestBookContent content);

    @Operation(summary = "방명록 방 목록 조회", description = "방명록 방들을 검색어와 정렬방식에 따라 목록을 조회하는 API")
    @ApiErrorCodes({ErrorCode.USER_NOT_EXIST, ErrorCode.JWT_TOKEN_EXPIRED,ErrorCode.TOKEN_REQUIRED, ErrorCode.INVALID_JWT_TOKEN})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "방의 모든 방명록 조회 성공",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = GuestBookListResponse.class))))
    })
    ResponseEntity<?> getGuestBookRoomList(@RequestParam("sparkUserId") Long sparkUserId,
                                                            @RequestParam(required = false) String search,
                                                            @RequestParam(required = false) String sortBy);

    @Operation(summary = "방명록 방 삭제", description = "방명록 보관함에서 방명록을 삭제할 때 사용하는 API")
    @ApiErrorCodes({ErrorCode.USER_NOT_EXIST, ErrorCode.JWT_TOKEN_EXPIRED,ErrorCode.TOKEN_REQUIRED, ErrorCode.INVALID_JWT_TOKEN})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "방명록 방 삭제 성공")
    })
    ResponseEntity<?> deleteGuestBookRoom(@RequestParam Long sparkUserId,
                                                        @PathVariable("roomId") Long roomId);
//
//    @Operation(summary = "명함 삭제", description = "명함 삭제를 위한 API ")
//    @ApiErrorCodes({ErrorCode.CARD_NOT_EXIST, ErrorCode.JWT_TOKEN_EXPIRED,ErrorCode.TOKEN_REQUIRED,
//            ErrorCode.INVALID_JWT_TOKEN,ErrorCode.NOT_YOUR_CARD })
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "명함 삭제 성공",
//            content = @Content(mediaType = "application/json", schema = @Schema(type = "Map", example = """
//                   {
//                        "DELETE COMPLETED" : 1
//                    }
//                    """)))
//    })
//    Map<String, Long> deleteCard(@PathVariable("cardId") Long cardId);
//
//    @Operation(summary = "명함 수정", description = "명함 수정을 위한 API")
//    @ApiErrorCodes({ErrorCode.CARD_NOT_EXIST, ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED,
//            ErrorCode.INVALID_JWT_TOKEN, ErrorCode.NOT_YOUR_CARD})
//    @ApiResponse(responseCode = "200", description = "명함 수정 성공",
//            content = @Content(mediaType = "application/json", schema = @Schema(type = "Map", example = """
//                    {
//                        "UPDATED" : 1
//                    }
//                    """)))
//    Map<String, Long> modifyCard(@PathVariable("cardId") Long cardId,
//                                 @RequestBody @Valid CardCreateDTO cardCreateDTO);
//


}
