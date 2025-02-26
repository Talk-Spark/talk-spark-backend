package mutsa.yewon.talksparkbe.domain.game.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mutsa.yewon.talksparkbe.domain.game.controller.dto.EndGameDto;
import mutsa.yewon.talksparkbe.domain.game.controller.dto.EndGameResponseDto;
import mutsa.yewon.talksparkbe.global.dto.ResponseDTO;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import mutsa.yewon.talksparkbe.global.swagger.ApiErrorCodes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "게임 종료 API")
public interface GameEndControllerDocs {

    @Operation(summary = "게임 종료 처리 API", description = "게임 종료 시 최종 점수를 공개하고 사용자 명함들을 모두 저장합니다.")
    @ApiErrorCodes({ErrorCode.GAME_NOT_FOUND})
    ResponseEntity<ResponseDTO<EndGameResponseDto>> endGame(@RequestBody EndGameDto endGameDto);
}
