package mutsa.yewon.talksparkbe.domain.game;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomCreateRequest;
import mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse.RoomCreateResponse;
import mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse.RoomDetailsResponse;
import mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse.RoomListResponse;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import mutsa.yewon.talksparkbe.global.swagger.ApiErrorCodes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "방 생성 및 조회와 관련된 API")
public interface RoomControllerDocs {

    @Operation(summary = "방 생성 API", description = "게임을 진행하기 위해 방을 생성하는 API")
    @ApiErrorCodes({ErrorCode.JWT_TOKEN_EXPIRED, ErrorCode.TOKEN_REQUIRED, ErrorCode.USER_NOT_EXIST})
    ResponseEntity<RoomCreateResponse> roomCreate(@RequestBody RoomCreateRequest roomCreateRequest,
                                                  @RequestHeader("Authorization") String token);

    @Operation(summary = "방 검색 API", description = "방 이름을 통해서 방을 검색하는 API")
    ResponseEntity<List<RoomListResponse>> roomSearch(@RequestParam String searchName);

    @Operation(summary = "방 정보 검색 API", description = "방 id 기반으로 방 정보 검색 API")
    ResponseEntity<RoomDetailsResponse> roomDetails(@PathVariable Long roomId);

    @Operation(summary = "방장 확인 API")
    ResponseEntity<Boolean> isHost(@RequestParam Long roomId,
                                   @RequestHeader("Authorization") String token);

    @Operation(summary = "방 중복 확인 API", description = "방 이름 기반으로 방 중복을 확인하는 API")
    ResponseEntity<Boolean> isDuplicate(@RequestParam String roomName);

    @Operation(summary = "질문 팁을 전달하는 API",description = "특정 명함 항목에 맞는 질문 팁을 전달하는 API")
    ResponseEntity<String> questionTip(@RequestParam String field);


}
