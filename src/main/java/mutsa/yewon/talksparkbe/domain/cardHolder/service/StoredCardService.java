package mutsa.yewon.talksparkbe.domain.cardHolder.service;

import mutsa.yewon.talksparkbe.domain.cardHolder.dto.IndCardHolderCreateDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.TeamCardHolderCreateDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.CardHolderListDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.StoredCardDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
public interface StoredCardService {

    Long storeIndCard(IndCardHolderCreateDTO indCardHolderCreateDTO);
    Long storeTeamCard(TeamCardHolderCreateDTO teamCardHolderCreateDTO);
    List<StoredCardDTO> getCardHolderDTO(Long cardHolderId);
    CardHolderListDTO getCardHolderDTOs(String searchType, Long sparkUserId);
    Map<String, Long> bookMarkCard(Long cardHolderId);
    Map<String, Long> deleteCardHolder(Long cardHolderId);
}
