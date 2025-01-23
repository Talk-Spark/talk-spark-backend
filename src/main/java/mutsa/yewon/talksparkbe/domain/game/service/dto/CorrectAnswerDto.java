package mutsa.yewon.talksparkbe.domain.game.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import mutsa.yewon.talksparkbe.domain.card.entity.CardThema;

@Data
@AllArgsConstructor
@Builder
@Setter
public class CorrectAnswerDto {

    private Long sparkUserId;
    private String name;
    private boolean isCorrect;
    private CardThema color;

}
