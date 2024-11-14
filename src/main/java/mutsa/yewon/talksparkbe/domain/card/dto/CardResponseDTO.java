package mutsa.yewon.talksparkbe.domain.card.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseDTO {

    private Long id;

    private String kakaoId;

    private String name;

    private Integer age;

    private String major;

    private String mbti;

    private String hobby;

    private String lookAlike;

    private String slogan;

    private String tmi;

    public static CardResponseDTO fromCard(Card card) {
        return CardResponseDTO.builder()
                .id(card.getId())
                .age(card.getAge())
                .hobby(card.getHobby())
                .lookAlike(card.getLookAlike())
                .slogan(card.getSlogan())
                .tmi(card.getTmi())
                .name(card.getName())
                .kakaoId(card.getSparkUser().getKakaoId())
                .major(card.getMajor())
                .mbti(card.getMbti())
                .build();
    }
}
