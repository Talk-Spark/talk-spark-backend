package mutsa.yewon.talksparkbe.domain.card.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.dto.CardCreateDTO;
import mutsa.yewon.talksparkbe.domain.card.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping("/api/cards")
    public ResponseEntity<Long> createCard(@Valid @RequestBody  CardCreateDTO cardCreateDTO) {
        Long cardId = cardService.createCard(cardCreateDTO);

        return ResponseEntity.status(201).body(cardId);
    }
}
