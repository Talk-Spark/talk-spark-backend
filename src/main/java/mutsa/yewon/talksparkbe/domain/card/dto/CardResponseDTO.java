package mutsa.yewon.talksparkbe.domain.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(title = "명함 조회 데이터")
public class CardResponseDTO {

    @Schema(description = "사용자의 식별자", example = "1")
    private Long id;

    @Schema(description = "사용자의 카카오 ID", example = "333345555")
    private String kakaoId;

    @Schema(description = "이름", example = "박승범")
    private String name;

    @Schema(description = "나이", example = "24")
    private Integer age;

    @Schema(description = "전공/직무", example = "컴퓨터공학과")
    private String major;

    @Schema(description = "MBTI", example = "ISTJ")
    private String mbti;

    @Schema(description = "취미", example = "코딩")
    private String hobby;

    @Schema(description = "닮은꼴", example = "너구리")
    private String lookAlike;

    @Schema(description = "나는 이런사람이야", example = "코딩하는 너구리")
    private String slogan;

    @Schema(description = "TMI", example = "TALKSPARK")
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
