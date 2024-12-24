package mutsa.yewon.talksparkbe.domain.guestBook;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mutsa.yewon.talksparkbe.domain.card.dto.CardCreateDTO;
import mutsa.yewon.talksparkbe.domain.card.dto.CardResponseDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookContent;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookListResponse;
import mutsa.yewon.talksparkbe.global.dto.ResponseDTO;
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
            @ApiResponse(responseCode = "201", description = "방명록 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "status": 201,
                                                        "message": "방명록 내용이 작성되었습니다.",
                                                        "data": null
                                                    }
                                                    """
                                    )
                            }))
    })
    ResponseEntity<?> postGuestBook(@PathVariable("roomId") Long roomId,
                                    @Valid @RequestBody GuestBookContent content);

    @Operation(summary = "방명록 방 목록 조회", description = "방명록 방들을 검색어와 정렬방식에 따라 목록을 조회하는 API")
    @ApiErrorCodes({ErrorCode.USER_NOT_EXIST, ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED, ErrorCode.INVALID_JWT_TOKEN})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "방의 모든 방명록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "status": 200,
                                                      "message": "방명록 방들이 조회되었습니다.",
                                                      "data": [
                                                        {
                                                          "roomId": 1,
                                                          "roomName": "멋사멋사",
                                                          "roomDateTime": "2024-12-22T18:32:33.318Z",
                                                          "guestBookData": [
                                                            {
                                                              "guestBookId": 1,
                                                              "sparkUserName": "김멋사",
                                                              "guestBookContent": "오늘 정말 재밌었어요!!",
                                                              "guestBookDateTime": "2024-12-22T18:32:33.319Z",
                                                              "ownerGuestBook": true,
                                                              "cardThema": "YELLOW"
                                                            },
                                                            {
                                                              "guestBookId": 2,
                                                              "sparkUserName": "김멋사2",
                                                              "guestBookContent": "오늘 정말 재밌었어요!!",
                                                              "guestBookDateTime": "2024-12-22T18:32:33.319Z",
                                                              "ownerGuestBook": true,
                                                              "cardThema": "YELLOW"
                                                            },
                                                            {
                                                              "guestBookId": 3,
                                                              "sparkUserName": "김멋사3",
                                                              "guestBookContent": "오늘 정말 재밌었어요!!",
                                                              "guestBookDateTime": "2024-12-22T18:32:33.319Z",
                                                              "ownerGuestBook": true,
                                                              "cardThema": "YELLOW"
                                                            }
                                                          ],
                                                          "guestBookFavorited": true
                                                        },
                                                        {
                                                          "roomId": 2,
                                                          "roomName": "멋사멋사2",
                                                          "roomDateTime": "2024-12-22T18:32:33.318Z",
                                                          "guestBookData": [
                                                            {
                                                              "guestBookId": 1,
                                                              "sparkUserName": "김멋사",
                                                              "guestBookContent": "오늘 정말 재밌었어요!!",
                                                              "guestBookDateTime": "2024-12-22T18:32:33.319Z",
                                                              "ownerGuestBook": true,
                                                              "cardThema": "YELLOW"
                                                            },
                                                            {
                                                              "guestBookId": 2,
                                                              "sparkUserName": "김멋사2",
                                                              "guestBookContent": "오늘 정말 재밌었어요!!",
                                                              "guestBookDateTime": "2024-12-22T18:32:33.319Z",
                                                              "ownerGuestBook": true,
                                                              "cardThema": "YELLOW"
                                                            },
                                                            {
                                                              "guestBookId": 3,
                                                              "sparkUserName": "김멋사3",
                                                              "guestBookContent": "오늘 정말 재밌었어요!!",
                                                              "guestBookDateTime": "2024-12-22T18:32:33.319Z",
                                                              "ownerGuestBook": true,
                                                              "cardThema": "YELLOW"
                                                            }
                                                          ],
                                                          "guestBookFavorited": true
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )}
                    ))
    })
    ResponseEntity<?> getGuestBookRoomList(@RequestParam(required = false) String search,
                                           @RequestParam(required = false) String sortBy);

    @Operation(summary = "방명록 방 삭제", description = "방명록 보관함에서 방명록 방을 삭제할 때 사용하는 API")
    @ApiErrorCodes({ErrorCode.USER_NOT_EXIST, ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED, ErrorCode.INVALID_JWT_TOKEN})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "방명록 방 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                                    examples={
                            @ExampleObject(
                                    value = """
                                            {
                                                "status": 200,
                                                "message": "방명록 방이 삭제되었습니다.",
                                                "data": null
                                            }
                                            """
                            )
                    }))
    })
    ResponseEntity<?> deleteGuestBookRoom(@PathVariable("roomId") Long roomId);


    @Operation(summary = "방명록 목록 조회", description = "각 방명록 방에 저장되어있는 방명록 내용 목룍을 조회하는 API")
    @ApiErrorCodes({ErrorCode.USER_NOT_EXIST, ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED, ErrorCode.INVALID_JWT_TOKEN})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "방 방명록 내용 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                    {
                                        "status": 200,
                                        "message": "방명록 내용이 조회되었습니다.",
                                        "data": {
                                            "roomId": 1,
                                            "roomName": "멋사멋사",
                                            "roomDateTime": "2024-12-22T19:02:27.248Z",
                                            "guestBookData": [
                                                {
                                                 "guestBookId": 1,
                                                 "sparkUserName": "김멋사",
                                                 "guestBookContent": "오늘 정말 재밌었어요!!",
                                                 "guestBookDateTime": "2024-12-22T19:02:27.248Z",
                                                 "ownerGuestBook": true,
                                                 "cardThema": "YELLOW"
                                                },
                                                {
                                                 "guestBookId": 2,
                                                 "sparkUserName": "김멋사2",
                                                 "guestBookContent": "오늘 정말 재밌었어요!!",
                                                 "guestBookDateTime": "2024-12-22T19:02:27.248Z",
                                                 "ownerGuestBook": true,
                                                 "cardThema": "YELLOW"
                                                }
                                            ],
                                            "guestBookFavorited": true
                                        }
                                    }
                                            """
                                    )
                            }))
    })
    ResponseEntity<?> getGuestBookList(@PathVariable("roomId") Long roomId);

    @Operation(summary = "방 방명록 즐겨찾기 추가", description = "방명록 보관함에서 방명록 방 즐겨찾기를 추가하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "방명록 방 즐겨찾기 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "status": 200,
                                                        "message": "방명록 방 즐겨찾기가 수정되었습니다.",
                                                        "data": null
                                                    }
                                                    """
                                    )
                            }))
    })
    ResponseEntity<?> UpdateGuestBookRoomFavorites(@PathVariable("roomId") Long roomId, @RequestParam("isFavorited") boolean isFavorited);
}
