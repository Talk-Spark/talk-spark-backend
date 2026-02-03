package mutsa.yewon.talksparkbe.domain.game.controller.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.card.entity.CardThema;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameCardInfo {

    private Long cardId;
    private Long userId;
    private String userName;
    private String name;
    private Integer age;
    private String major;
    private String mbti;
    private String hobby;
    private String lookAlike;
    private String slogan;
    private String tmi;
    private CardThema cardThema;

    public static GameCardInfo from(Card card) {
        return GameCardInfo.builder()
                .cardId(card.getId())
                .userId(card.getSparkUser().getId())
                .userName(card.getSparkUser().getName())
                .name(card.getName())
                .age(card.getAge())
                .major(card.getMajor())
                .mbti(card.getMbti())
                .hobby(card.getHobby())
                .lookAlike(card.getLookAlike())
                .slogan(card.getSlogan())
                .tmi(card.getTmi())
                .cardThema(card.getCardThema())
                .build();
    }
}
