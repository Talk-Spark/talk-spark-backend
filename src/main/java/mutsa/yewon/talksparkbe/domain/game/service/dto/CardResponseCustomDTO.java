package mutsa.yewon.talksparkbe.domain.game.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.card.entity.CardThema;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseCustomDTO {

    private Long id;

    private String kakaoId;

    private String name;

    private Integer age;

    private String major;

    private String mbti;

    private String hobby;

    private String lookAlike;

    private String selfDescription;

    private String tmi;

    private Long ownerId;

    private CardThema cardThema;

    public static CardResponseCustomDTO fromCard(Card card) {
        return CardResponseCustomDTO.builder()
                .id(card.getId())
                .age(card.getAge())
                .hobby(card.getHobby())
                .lookAlike(card.getLookAlike())
                .selfDescription(card.getSlogan())
                .tmi(card.getTmi())
                .name(card.getName())
                .kakaoId(card.getSparkUser().getKakaoId())
                .major(card.getMajor())
                .mbti(card.getMbti())
                .ownerId(card.getSparkUser().getId())
                .cardThema(card.getCardThema())
                .build();
    }

}
