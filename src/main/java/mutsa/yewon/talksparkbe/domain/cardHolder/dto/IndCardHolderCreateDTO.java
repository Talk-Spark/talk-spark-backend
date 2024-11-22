package mutsa.yewon.talksparkbe.domain.cardHolder.dto;

import lombok.Data;

@Data
public class IndCardHolderCreateDTO {

    private String storeType;

    private String name;

    private Long cardId;

    private Long sparkUserId;

}
