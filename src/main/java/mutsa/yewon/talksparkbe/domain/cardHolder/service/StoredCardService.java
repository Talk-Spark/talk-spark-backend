package mutsa.yewon.talksparkbe.domain.cardHolder.service;

import mutsa.yewon.talksparkbe.domain.cardHolder.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
public interface StoredCardService {

    Long storeIndCard(IndCardHolderCreateDTO indCardHolderCreateDTO);
    Long storeTeamCard(TeamCardHolderCreateDTO teamCardHolderCreateDTO);
    List<StoredCardDTO> getCardHolderDTO(Long cardHolderId);
    CardHolderListDTO getCardHolderDTOs(String searchType, Long sparkUserId);
    Map<String, Long> bookMarkCard(Long cardHolderId);
    Map<String, Long> deleteCardHolder(Long cardHolderId);
    CardHolderListDTO getCardHolderByName(String searchType);
    List<MainPageCardHolderDTO> getMainPageCards(Long sparkUserId);
}
