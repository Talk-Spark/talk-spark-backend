package mutsa.yewon.talksparkbe.domain.game.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardBlanksDto;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardQuestion;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardResponseCustomDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBroadcastDTO {

    private CardResponseCustomDTO currentCard;
    private CardBlanksDto currentBlanks;
    private CardQuestion question;
    private String roomName;
}