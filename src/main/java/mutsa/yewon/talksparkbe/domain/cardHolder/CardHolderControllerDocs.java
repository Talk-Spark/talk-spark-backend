package mutsa.yewon.talksparkbe.domain.cardHolder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.CardHolderDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.CardHolderListDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.IndCardHolderCreateDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.TeamCardHolderCreateDTO;
import mutsa.yewon.talksparkbe.global.dto.ResponseDTO;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import mutsa.yewon.talksparkbe.global.swagger.ApiErrorCodes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "명함 보관함 API", description = "개별 명함, 팀별 명함에 대한 저장/조회/삭제를 수행합니다.")
public interface CardHolderControllerDocs {

    @Operation(summary = "개별 명함 저장", description = "사용자의 개별 명함을 QR코드로 받아 저장하는 API")
    @ApiErrorCodes({ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED, ErrorCode.CARD_NOT_EXIST})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "개별 명함 저장 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "status": 201,
                                                "message": "개별명함이 저장되었습니다.",
                                                "data": {
                                                    "cardHolderId": 7
                                                }
                                            }
                                            """
                            )
                    }))
    })
    ResponseEntity<?> storeIndCard(@RequestBody IndCardHolderCreateDTO indCardHolderCreateDTO);


    @Operation(summary = "팀별 명함 저장", description = "게임이 종료된 이후 팀별 명함을 모두 저장하는 API")
    @ApiErrorCodes({ErrorCode.USER_NOT_EXIST, ErrorCode.CARD_NOT_EXIST, ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "개별 명함 저장 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "status": 201,
                                                "message": "팀별명함이 저장되었습니다.",
                                                "data": {
                                                    "cardHolderId": 8
                                                }
                                            }
                                            """
                            )
                    }))
    })
    ResponseEntity<?> storeTeamCard(@RequestBody TeamCardHolderCreateDTO teamCardHolderCreateDTO);

    @Operation(summary = "명함 보관함 속 명함 조회", description = "명함 보관함에 저장된 명함 객체 한개를 조회하는 API")
    @ApiErrorCodes({ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED, ErrorCode.CARDHOLDER_NOT_EXIST})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "명함 객체 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class),examples = {
                    @ExampleObject(
                            value = """
                                    {
                                        "status": 200,
                                        "message": "모든 팀원들의 명함을 조회합니다.",
                                        "data": [
                                            {
                                                "storedCardId": 2,
                                                "name": "박승범",
                                                "age": 24,
                                                "major": "컴퓨터공학과",
                                                "mbti": "ISTJ",
                                                "hobby": "코딩",
                                                "lookAlike": "너구리",
                                                "slogan": "개발자가 되",
                                                "tmi": "TalkSpark!!!",
                                                "cardThema": "PINK",
                                                "bookMark": false,
                                                "cardHolderName": "멋사우주최강"
                                            },
                                            {
                                                "storedCardId": 3,
                                                "name": "박승범",
                                                "age": 24,
                                                "major": "컴퓨터공학과",
                                                "mbti": "ISTJ",
                                                "hobby": "코딩",
                                                "lookAlike": "너구리",
                                                "slogan": "개발자가 되",
                                                "tmi": "TalkSpark!!!",
                                                "cardThema": "MINT",
                                                "bookMark": false,
                                                "cardHolderName": "멋사우주최강"
                                            }
                                        ]
                                    }
                                    """
                    )
            }))
    })
    ResponseEntity<?> getStoredCard(@PathVariable Long cardHolderId);

    @Operation(summary = "명함 보관함 속 명함 객체들 조회", description = """
            명함 보관함 페이지에서 각 정렬조건에 맞는 명함 객체들을 조회하는 API
            
            정렬 조건
            1. Alphabet : 가나다순
            2. Bookmark : 보관된 명함들 중 즐겨찾기 된 명함들만 조회
            3. 빈칸 : 최신순 조회
            """ )
    @ApiErrorCodes({ErrorCode.USER_NOT_EXIST, ErrorCode.CARDHOLDER_NOT_EXIST, ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "명함 객체 리스트 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class),examples = {
                    @ExampleObject(
                            value = """
                                    {
                                        "status": 200,
                                        "message": "정렬 조건에 따라 보관된 명함들을 조회합니다.",
                                        "data": {
                                            "numOfCards": 3,
                                            "searchType": "Default",
                                            "cardHolders": [
                                                {
                                                    "cardHolderId": 8,
                                                    "cardHolderName": "멋사우주최강",
                                                    "numOfTeammates": 2,
                                                    "teamNames": [
                                                        "박승범",
                                                        "박승범"
                                                    ],
                                                    "bookMark": false,
                                                    "storedAt": "2024-12-19T14:32:08.602231"
                                                },
                                                {
                                                    "cardHolderId": 7,
                                                    "cardHolderName": "박승범",
                                                    "numOfTeammates": 1,
                                                    "teamNames": [
                                                        "박승범"
                                                    ],
                                                    "bookMark": false,
                                                    "storedAt": "2024-12-19T14:30:38.027436"
                                                },
                                                {
                                                    "cardHolderId": 6,
                                                    "cardHolderName": "박승범",
                                                    "numOfTeammates": 1,
                                                    "teamNames": [
                                                        "박승범"
                                                    ],
                                                    "bookMark": true,
                                                    "storedAt": "2024-12-19T01:19:40.331608"
                                                }
                                            ]
                                        }
                                    }
                                    """
                    )
            }))
    })
    public ResponseEntity<?> getStoredCards(@RequestParam String searchType);

    @Operation(summary = "명함 객체에 대한 즐겨찾기", description = "명함 객체에 대한 즐겨찾기를 수행하는 API")
    @ApiErrorCodes({ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED, ErrorCode.CARDHOLDER_NOT_EXIST})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "명함 객체 즐겨찾기 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDTO.class),examples = {
                    @ExampleObject(
                            value = """
                                    {
                                        "status": 200,
                                        "message": "보관된 명함을 즐겨찾기 합니다.",
                                        "data": {
                                            "cardHolderId": 7
                                        }
                                    }
                                    """
                    )
            }))
    })
    ResponseEntity<?> bookMarkCard(@PathVariable Long cardHolderId);

    @Operation(summary = "명함 객체 삭제", description = "명함 객체 삭제")
    @ApiErrorCodes({ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED, ErrorCode.CARDHOLDER_NOT_EXIST})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "명함 객체 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "status": 200,
                                                "message": "보관된 명함을 삭제합니다",
                                                "data": {
                                                    "cardHolderId": 7
                                                }
                                            }
                                    """
                            )
                    }))
    })
    ResponseEntity<?> deleteCardHolder(@PathVariable Long cardHolderId);

    @Operation(summary = "명함 검색", description = "팀 또는 사용자 이름을 기준으로 검색하는 API")
    @ApiErrorCodes({ErrorCode.NO_MATCHING_CARDHOLDER, ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED, ErrorCode.INTERNAL_SERVER_ERROR})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "명함 검색 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class),examples = {
                    @ExampleObject(
                            value = """
                                    {
                                        "status": 200,
                                        "message": "팀 또는 사용자의 이름에 해당하는 명함들입니다.",
                                        "data": {
                                            "numOfCards": 1,
                                            "searchType": "멋사우주최강",
                                            "cardHolders": [
                                                {
                                                    "cardHolderId": 2,
                                                    "cardHolderName": "멋사우주최강",
                                                    "numOfTeammates": 2,
                                                    "teamNames": [
                                                        "박승범",
                                                        "박승범"
                                                    ],
                                                    "bookMark": false,
                                                    "storedAt": "2024-12-26T14:53:30.626356"
                                                }
                                            ]
                                        }
                                    }
                                    """
                    )
            }))
    })
    ResponseEntity<?> getCardHoldersByName(@RequestParam String name);
}
