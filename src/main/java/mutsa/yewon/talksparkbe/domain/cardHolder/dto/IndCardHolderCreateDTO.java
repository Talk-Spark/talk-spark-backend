package mutsa.yewon.talksparkbe.domain.cardHolder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(title = "개별 명함 저장 요청")
public class IndCardHolderCreateDTO {

    @Schema(description = "저장 형식", example = "IND")
    private String storeType;

    @Schema(description = "저장하고자 하는 명함 주인 이름", example = "박승범")
    private String name;

    @Schema(description = "저장하고자 하는 명함 식별자", example = "1")
    private Long cardId;

    @Schema(description = "명함 보관함 사용자 식별자", example = "2")
    private Long sparkUserId;

}
