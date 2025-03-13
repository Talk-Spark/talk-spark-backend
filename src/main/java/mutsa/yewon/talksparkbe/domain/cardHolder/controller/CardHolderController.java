package mutsa.yewon.talksparkbe.domain.cardHolder.controller;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.cardHolder.CardHolderControllerDocs;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.*;
import mutsa.yewon.talksparkbe.domain.cardHolder.service.StoredCardService;
import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
import mutsa.yewon.talksparkbe.global.dto.ResponseDTO;
import mutsa.yewon.talksparkbe.global.util.SecurityUtil;
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
        return ResponseEntity.status(201).body(ResponseDTO.created("개별명함이 저장되었습니다.",
                Map.of("cardHolderId", cardHolderId)));
    }

    @PostMapping("/api/store/team")
    public ResponseEntity<?> storeTeamCard(@RequestBody TeamCardHolderCreateDTO teamCardHolderCreateDTO) {
        Long cardHolderId = storedCardService.storeTeamCard(teamCardHolderCreateDTO);
        return ResponseEntity.status(201).body(ResponseDTO.created("팀별명함이 저장되었습니다.",
                Map.of("cardHolderId", cardHolderId)));
    }

    @GetMapping("/api/storedCard/{cardHolderId}")
    public ResponseEntity<?> getStoredCard(@PathVariable Long cardHolderId) {
        List<StoredCardDTO> storedCardDTOS = storedCardService.getCardHolderDTO(cardHolderId);
        return ResponseEntity.status(200).body(ResponseDTO.ok("모든 팀원들의 명함을 조회합니다.", storedCardDTOS));
    }

    @GetMapping("/api/storedCards")
    public ResponseEntity<?> getStoredCards(@RequestParam(required = false, defaultValue = "Default") String searchType) {

        CardHolderListDTO cardHolderListDTOs = storedCardService.getCardHolderDTOs(searchType,
                SecurityUtil.getLoggedInUserId());

        return ResponseEntity.status(200).body(ResponseDTO.ok("정렬 조건에 따라 보관된 명함들을 조회합니다.",
                cardHolderListDTOs));
    }

    @PutMapping("/api/storedCard/{cardHolderId}")
    public ResponseEntity<?> bookMarkCard(@PathVariable Long cardHolderId) {

        return ResponseEntity.ok(ResponseDTO.ok("보관된 명함을 즐겨찾기 합니다.",
                storedCardService.bookMarkCard(cardHolderId)));
    }

    @DeleteMapping("/api/storedCard/{cardHolderId}")
    public ResponseEntity<?> deleteCardHolder(@PathVariable Long cardHolderId){
        Map<String, Long> response = storedCardService.deleteCardHolder(cardHolderId);

        return ResponseEntity.ok(ResponseDTO.ok("보관된 명함을 삭제합니다", response));
    }

    @GetMapping("/api/storedCards/search")
    public ResponseEntity<?> getCardHoldersByName(@RequestParam String name) {
        CardHolderListDTO cardHolderByName = storedCardService.getCardHolderByName(name);

        return ResponseEntity.ok(ResponseDTO.ok("팀 또는 사용자의 이름에 해당하는 명함들입니다.", cardHolderByName));
    }

    @GetMapping("/api/storedCards/main")
    public ResponseDTO<List<MainPageCardHolderDTO>> getMainPageCards(){
        List<MainPageCardHolderDTO> mainPageCards = storedCardService.getMainPageCards(SecurityUtil.getLoggedInUserId());

        return ResponseDTO.ok("메인 페이지에 사용될 보관된 명함들 입니다.", mainPageCards);
    }
}
