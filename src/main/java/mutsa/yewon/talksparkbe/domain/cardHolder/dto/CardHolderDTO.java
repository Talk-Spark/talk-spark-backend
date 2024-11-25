package mutsa.yewon.talksparkbe.domain.cardHolder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.cardHolder.entity.CardHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardHolderDTO {

    private Long cardHolderId;

    private String cardHolderName;

    @Builder.Default
    private List<String> teamNames = new ArrayList<>();

    private boolean bookMark;

    private LocalDateTime storedAt;

//    @Builder.Default
//    private List<StoredCardDTO> storedCards = new ArrayList<>();

    public static CardHolderDTO entitytoDTO(CardHolder cardHolder) {
        CardHolderDTO cardHolderDTO = CardHolderDTO.builder()
                .cardHolderId(cardHolder.getId())
                .cardHolderName(cardHolder.getName())
                .bookMark(cardHolder.isBookMark())
                .storedAt(cardHolder.getStoredAt())
                .build();

//        List<StoredCardDTO> storedCardDTOS = cardHolder.getStoredCards().stream()
//                .map(StoredCardDTO::entityToDTO).toList();
//
//        cardHolderDTO.setStoredCards(storedCardDTOS);

        for(String teammateName : cardHolder.getTeammates()){
            cardHolderDTO.getTeamNames().add(teammateName);
        }

        return cardHolderDTO;
    }
}
