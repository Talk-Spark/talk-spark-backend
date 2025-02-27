package mutsa.yewon.talksparkbe.domain.game.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "게임 종료 후 참여한 인원의 명함 정보를 반환하는 DTO")
public class CardResponseCustomDTO {

    @Schema(description = "카드의 고유 ID", example = "1")
    private Long id;

    @Schema(description = "카카오 로그인 ID", example = "kakao_123456")
    private String kakaoId;

    @Schema(description = "사용자 이름", example = "김철수")
    private String name;

    @Schema(description = "사용자 나이", example = "25")
    private Integer age;

    @Schema(description = "사용자 전공", example = "컴퓨터공학")
    private String major;

    @Schema(description = "사용자의 MBTI", example = "INTP")
    private String mbti;

    @Schema(description = "사용자의 취미", example = "축구")
    private String hobby;

    @Schema(description = "닮은 연예인", example = "박보검")
    private String lookAlike;

    @Schema(description = "사용자 자기소개", example = "안녕하세요! 저는 개발자입니다.")
    private String selfDescription;

    @Schema(description = "사용자의 TMI", example = "라면을 좋아해요.")
    private String tmi;

    @Schema(description = "카드 소유자의 ID", example = "1001")
    private Long ownerId;

    @Schema(description = "카드의 테마", example = "BLUE")
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
