package mutsa.yewon.talksparkbe.domain.game.controller;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.controller.dto.GameScoreDto;
import mutsa.yewon.talksparkbe.domain.game.service.GameService;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardResponseCustomDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/api/game/end")
    public ResponseEntity<?> endGame(@PathVariable Long roomId){
        // 점수판, 참가자 카드들 모두 조회
        Map<Long, Integer> scores = gameService.getScores(roomId);

        List<CardResponseCustomDTO> playerCards = gameService.getAllRelatedCards(roomId);

        // 전달
        return ResponseEntity.ok(GameScoreDto.from(scores, playerCards));
    }



}
