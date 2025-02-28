package mutsa.yewon.talksparkbe.domain.game.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.controller.dto.EndGameDto;
import mutsa.yewon.talksparkbe.domain.game.controller.dto.EndGameResponseDto;
import mutsa.yewon.talksparkbe.domain.game.controller.swagger.GameEndControllerDocs;
import mutsa.yewon.talksparkbe.domain.game.service.GameService;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardResponseCustomDTO;
import mutsa.yewon.talksparkbe.global.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GameEndController implements GameEndControllerDocs {

    private final GameService gameService;

    @PostMapping("/api/game/end")
    public ResponseEntity<ResponseDTO<EndGameResponseDto>> endGame(@RequestBody EndGameDto endGameDto) {
        return ResponseEntity.ok(ResponseDTO.ok("플레이어 명함 저장 완료.",
                gameService.endGame(endGameDto)));
    }

    @DeleteMapping("/api/game/{gameId}")
    public ResponseEntity<ResponseDTO<?>> changeGameStatus(@PathVariable Long gameId){
        gameService.removeGameState(gameId);

        return ResponseEntity.ok(ResponseDTO.ok("게임이 종료되었습니다."));
    }


}
