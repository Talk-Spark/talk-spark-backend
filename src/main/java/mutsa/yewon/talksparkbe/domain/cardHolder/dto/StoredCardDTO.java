package mutsa.yewon.talksparkbe.domain.cardHolder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.entity.CardThema;
import mutsa.yewon.talksparkbe.domain.cardHolder.entity.StoredCard;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoredCardDTO {

    private Long storedCardId;

    private String name;

    private Integer age;

    private String major;

    private String mbti;

    private String hobby;

    private String lookAlike;

    private String slogan;

    private String tmi;

    private CardThema cardThema;

    public static StoredCardDTO entityToDTO(StoredCard storedCard) {
        return StoredCardDTO.builder()
                .storedCardId(storedCard.getId())
                .name(storedCard.getName())
                .age(storedCard.getAge())
                .major(storedCard.getMajor())
                .mbti(storedCard.getMbti())
                .hobby(storedCard.getHobby())
                .lookAlike(storedCard.getLookAlike())
                .slogan(storedCard.getSlogan())
                .tmi(storedCard.getTmi())
                .cardThema(storedCard.getCardThema())
                .build();
    }
}
