package mutsa.yewon.talksparkbe.domain.card.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.dto.CardCreateDTO;
import mutsa.yewon.talksparkbe.domain.card.dto.CardResponseDTO;
import mutsa.yewon.talksparkbe.domain.card.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping("/api/cards")
    public ResponseEntity<Long> createCard(@Valid @RequestBody CardCreateDTO cardCreateDTO) {
        Long cardId = cardService.createCard(cardCreateDTO);

        return ResponseEntity.status(201).body(cardId);
    }

    @GetMapping("/api/cards")
    public ResponseEntity<List<CardResponseDTO>> getCards(@RequestParam("kakaoId") String kakaoId) {
        List<CardResponseDTO> cards = cardService.getCards(kakaoId);
        return ResponseEntity.status(200).body(cards);
    }

    @GetMapping("/api/cards/{cardId}")
    public ResponseEntity<CardResponseDTO> getCard(@PathVariable("cardId") Long cardId) {
        CardResponseDTO card = cardService.getCard(cardId);
        return ResponseEntity.status(200).body(card);
    }
}
