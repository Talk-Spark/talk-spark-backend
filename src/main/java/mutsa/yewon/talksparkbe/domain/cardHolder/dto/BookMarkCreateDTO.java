package mutsa.yewon.talksparkbe.domain.cardHolder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookMarkCreateDTO {

    private Long cardId;

    private Long sparkUserId;
}
