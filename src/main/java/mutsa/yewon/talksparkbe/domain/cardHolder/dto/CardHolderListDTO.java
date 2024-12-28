package mutsa.yewon.talksparkbe.domain.cardHolder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardHolderListDTO {

    private int numOfCards;

    private String searchType;

    @Builder.Default
    private List<CardHolderDTO> cardHolders = new ArrayList<>();

}
