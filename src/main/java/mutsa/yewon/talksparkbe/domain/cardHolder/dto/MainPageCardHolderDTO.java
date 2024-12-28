package mutsa.yewon.talksparkbe.domain.cardHolder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.cardHolder.entity.CardHolder;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainPageCardHolderDTO {

    private String teamName;

    @Builder.Default
    private List<StoredCardDTO> cards = new ArrayList<>();

    public static MainPageCardHolderDTO entityToDTO(CardHolder cardHolder) {

        List<StoredCardDTO> storedCardDTOS = cardHolder.getStoredCards().stream()
                .map(storedCard -> StoredCardDTO.entityToDTO(storedCard, cardHolder)).toList();

        return MainPageCardHolderDTO.builder()
                .teamName(cardHolder.getName())
                .cards(storedCardDTOS)
                .build();
    }

}
