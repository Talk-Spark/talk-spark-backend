package mutsa.yewon.talksparkbe.domain.card.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;

@Data
public class CardCreateDTO {

    @NotBlank(message = "카카오 ID는 필수항목입니다.")
    private String kakaoId;

    @NotBlank(message = "이름은 필수항목입니다.")
    private String name;

    @NotNull(message = "나이는 필수항목입니다.")
    private Integer age;

    @NotBlank(message = "전공/직무는 필수항목입니다.")
    private String major;

    private String mbti;

    private String hobby;

    private String lookAlike;

    private String slogan;

    private String tmi;

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
                .build();
    }

}
