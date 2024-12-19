package mutsa.yewon.talksparkbe.domain.cardHolder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(title = "팀별 명함 저장 요청")
public class TeamCardHolderCreateDTO {

    @Schema(description = "저장 형식", example = "TEAM")
    private String storeType;

    @Schema(description = "명함 보관함 사용자 식별자", example = "1")
    private Long sparkUserId;

    @Schema(description = "팀 이름", example = "멋사 2팀")
    private String teamName;

    @Schema(description = "저장하고자 하는 팀원들의 명함 식별자", example = "[1,2]")
    private List<Long> cardIds = new ArrayList<>();


}
