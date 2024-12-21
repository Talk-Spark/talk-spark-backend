package mutsa.yewon.talksparkbe.domain.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.card.entity.CardThema;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;

@Data
@Schema(title = "명함 생성 요청 데이터")
@AllArgsConstructor
public class CardCreateDTO {

    @Schema(description = "사용자 식별자", example = "1")
    private Long sparkUserId ;

    @NotBlank(message = "이름은 필수항목입니다.")
    @Schema(description = "이름", example = "박승범")
    private String name;

    @NotNull(message = "나이는 필수항목입니다.")
    @Schema(description = "나이", example = "24")
    private Integer age;

    @NotBlank(message = "전공/직무는 필수항목입니다.")
    @Schema(description = "전공/직무", example = "컴퓨터공학과")
    private String major;

    @Schema(description = "MBTI", example = "ISTJ")
    private String mbti;

    @Schema(description = "취미", example = "코딩")
    private String hobby;

    @Schema(description = "닮은꼴", example = "너구리")
    private String lookAlike;

    @Schema(description = "나를 한마디로 표현하자면", example = "코딩")
    private String slogan;

    @Schema(description = "TMI", example = "TalkSpark")
    private String tmi;

    @Schema(description = "명함 테마", example = "BLUE")
    private CardThema cardThema;

    public static Card toCard(CardCreateDTO dto, SparkUser user) {
        return Card.builder()
                .sparkUser(user)
                .age(dto.getAge())
                .hobby(dto.getHobby())
                .lookAlike(dto.getLookAlike())
                .mbti(dto.getMbti())
                .name(dto.getName())
                .major(dto.getMajor())
                .tmi(dto.getTmi())
                .slogan(dto.getSlogan())
                .cardThema(dto.getCardThema())
                .build();
    }

}
