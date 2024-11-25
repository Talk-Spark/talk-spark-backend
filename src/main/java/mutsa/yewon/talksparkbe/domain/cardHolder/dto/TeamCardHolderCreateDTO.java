package mutsa.yewon.talksparkbe.domain.cardHolder.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeamCardHolderCreateDTO {

    private String storeType;

    private Long sparkUserId;

    private String teamName;

    private List<Long> cardIds = new ArrayList<>();


}
