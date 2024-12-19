package mutsa.yewon.talksparkbe.domain.card.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.CardControllerDocs;
import mutsa.yewon.talksparkbe.domain.card.dto.CardCreateDTO;
import mutsa.yewon.talksparkbe.domain.card.dto.CardResponseDTO;
import mutsa.yewon.talksparkbe.domain.card.service.CardService;
import mutsa.yewon.talksparkbe.global.dto.ResponseDTO;
import mutsa.yewon.talksparkbe.global.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CardController implements CardControllerDocs {

    private final CardService cardService;

    private final SecurityUtil securityUtil;

    @PostMapping("/api/cards")
    public ResponseEntity<?> createCard(@Valid @RequestBody CardCreateDTO cardCreateDTO) {

        Long cardId = cardService.createCard(cardCreateDTO);

        ResponseDTO<Map<String, Long>> card = ResponseDTO.created("명함이 생성되었습니다.", Map.of("cardId", cardId));

        return ResponseEntity.status(201).body(card);
    }

    @GetMapping("/api/cards")
    public ResponseEntity<?> getCards() {

        List<CardResponseDTO> cards = cardService.getCards(securityUtil.getLoggedInUserId());

        return ResponseEntity.status(200).body(ResponseDTO.ok("사용자 명함 조회 성공", cards));
    }

    @GetMapping("/api/cards/{cardId}")
    public ResponseEntity<?> getCard(@PathVariable("cardId") Long cardId) {

        CardResponseDTO card = cardService.getCard(cardId);

        return ResponseEntity.status(200).body(ResponseDTO.ok("명함 단건 조회", card));
    }

    @DeleteMapping("/api/cards/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable("cardId") Long cardId) {

        return ResponseEntity.status(200).body(ResponseDTO.ok("명함이 삭제되었습니다.",
                cardService.deleteCard(cardId)));

    }

    @PutMapping("/api/cards/{cardId}")
    public ResponseEntity<?> modifyCard(@PathVariable("cardId") Long cardId,
                                        @RequestBody @Valid CardCreateDTO cardCreateDTO) {


        return ResponseEntity.status(200).body(ResponseDTO.ok("명함이 수정되었습니다.",
                cardService.modifyCard(cardId, cardCreateDTO)));
    }
}
