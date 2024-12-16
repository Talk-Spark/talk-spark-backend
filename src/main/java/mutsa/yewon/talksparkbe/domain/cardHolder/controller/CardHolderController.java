package mutsa.yewon.talksparkbe.domain.cardHolder.controller;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.cardHolder.CardHolderControllerDocs;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.IndCardHolderCreateDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.TeamCardHolderCreateDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.CardHolderListDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.StoredCardDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.service.StoredCardService;
import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CardHolderController implements CardHolderControllerDocs {

    private final StoredCardService storedCardService;

    @PostMapping("/api/store/ind")
    public ResponseEntity<?> storeIndCard(@RequestBody IndCardHolderCreateDTO indCardHolderCreateDTO) {
        Long cardHolderId = storedCardService.storeIndCard(indCardHolderCreateDTO);
        return ResponseEntity.status(201).body(cardHolderId);
    }

    @PostMapping("/api/store/team")
    public ResponseEntity<?> storeTeamCard(@RequestBody TeamCardHolderCreateDTO teamCardHolderCreateDTO) {
        Long cardHolderId = storedCardService.storeTeamCard(teamCardHolderCreateDTO);
        return ResponseEntity.status(201).body(cardHolderId);
    }

    @GetMapping("/api/storedCard")
    public ResponseEntity<?> getStoredCard(@RequestParam Long cardHolderId) {
        List<StoredCardDTO> storedCardDTOS = storedCardService.getCardHolderDTO(cardHolderId);
        return ResponseEntity.status(200).body(storedCardDTOS);
    }

    @GetMapping("/api/storedCards")
    public ResponseEntity<?> getStoredCards(@RequestParam String searchType) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        SparkUserDTO user = (SparkUserDTO) authentication.getPrincipal();

        Long sparkUserId = user.getSparkUserId();
        CardHolderListDTO cardHolderListDTOs = storedCardService.getCardHolderDTOs(searchType, sparkUserId);

        return ResponseEntity.status(200).body(cardHolderListDTOs);
    }

    @PutMapping("/api/storedCard")
    public ResponseEntity<?> bookMarkCard(@RequestParam Long cardHolderId) {
        Map<String, Long> response = storedCardService.bookMarkCard(cardHolderId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/storedCard")
    public ResponseEntity<?> deleteCardHolder(@RequestParam Long cardHolderId){
        Map<String, Long> response = storedCardService.deleteCardHolder(cardHolderId);
        return ResponseEntity.ok(response);
    }
}
