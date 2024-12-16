package mutsa.yewon.talksparkbe.domain.cardHolder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.CardHolderDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.CardHolderListDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.IndCardHolderCreateDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.TeamCardHolderCreateDTO;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import mutsa.yewon.talksparkbe.global.swagger.ApiErrorCodes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "명함 보관함 API", description = "개별 명함, 팀별 명함에 대한 저장/조회/삭제를 수행합니다.")
public interface CardHolderControllerDocs {

    @Operation(summary = "개별 명함 저장", description = "사용자의 개별 명함을 QR코드로 받아 저장하는 API")
    @ApiErrorCodes({ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED, ErrorCode.CARD_NOT_EXIST})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "개별 명함 저장 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "Long", example = "1")))
    })
    ResponseEntity<?> storeIndCard(@RequestBody IndCardHolderCreateDTO indCardHolderCreateDTO);


    @Operation(summary = "팀별 명함 저장", description = "게임이 종료된 이후 팀별 명함을 모두 저장하는 API")
    @ApiErrorCodes({ErrorCode.USER_NOT_EXIST, ErrorCode.CARD_NOT_EXIST, ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "개별 명함 저장 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "Long", example = "1")))
    })
    ResponseEntity<?> storeTeamCard(@RequestBody TeamCardHolderCreateDTO teamCardHolderCreateDTO);

    @Operation(summary = "명함 보관함 속 명함 조회", description = "명함 보관함에 저장된 명함 객체 한개를 조회하는 API")
    @ApiErrorCodes({ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED, ErrorCode.CARDHOLDER_NOT_EXIST})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "명함 객체 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardHolderDTO.class)))
    })
    ResponseEntity<?> getStoredCard(@RequestParam Long cardHolderId);

    @Operation(summary = "명함 보관함 속 명함 객체들 조회", description = "명함 보관함 페이지에서 각 정렬조건에 맞는 명함 객체들을 조회하는 API")
    @ApiErrorCodes({ErrorCode.USER_NOT_EXIST, ErrorCode.CARDHOLDER_NOT_EXIST, ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "명함 객체 리스트 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardHolderListDTO.class)))
    })
    public ResponseEntity<?> getStoredCards(@RequestParam String searchType);

    @Operation(summary = "명함 객체에 대한 즐겨찾기", description = "명함 객체에 대한 즐겨찾기를 수행하는 API")
    @ApiErrorCodes({ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED, ErrorCode.CARDHOLDER_NOT_EXIST})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "명함 객체 즐겨찾기 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "Map<String, Long>", example = """
                           {
                               "UPDATED" : 1
                           }
                           """)))
    })
    ResponseEntity<?> bookMarkCard(@RequestParam Long cardHolderId);

    @Operation(summary = "명함 객체 삭제", description = "명함 객체 삭제")
    ResponseEntity<?> deleteCardHolder(@RequestParam Long cardHolderId);
}
