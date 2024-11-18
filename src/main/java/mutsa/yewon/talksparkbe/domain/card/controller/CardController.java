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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CardController implements CardControllerDocs {

    private final CardService cardService;

    @PostMapping("/api/cards")
    public ResponseEntity<Long> createCard(@Valid @RequestBody CardCreateDTO cardCreateDTO) {

        Long cardId = cardService.createCard(cardCreateDTO);

        return ResponseEntity.status(201).body(cardId);
    }

    @GetMapping("/api/cards")
    public ResponseEntity<List<CardResponseDTO>> getCards(@RequestParam("sparkUserId") Long sparkUserId) {

        List<CardResponseDTO> cards = cardService.getCards(sparkUserId);

        return ResponseEntity.status(200).body(cards);
    }

    @GetMapping("/api/cards/{cardId}")
    public ResponseEntity<CardResponseDTO> getCard(@PathVariable("cardId") Long cardId) {

        CardResponseDTO card = cardService.getCard(cardId);

        return ResponseEntity.status(200).body(card);
    }

    @DeleteMapping("/api/cards/{cardId}")
    public Map<String, Long> deleteCard(@PathVariable("cardId") Long cardId) {

        return cardService.deleteCard(cardId);
    }

    @PutMapping("/api/cards/{cardId}")
    public Map<String, Long> modifyCard(@PathVariable("cardId") Long cardId,
                                        @RequestBody @Valid CardCreateDTO cardCreateDTO) {

        Map<String, Long> response = cardService.modifyCard(cardId, cardCreateDTO);
        return response;
    }
}
