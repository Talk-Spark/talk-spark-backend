package mutsa.yewon.talksparkbe.domain.card.service;

import mutsa.yewon.talksparkbe.domain.card.dto.CardCreateDTO;
import mutsa.yewon.talksparkbe.domain.card.dto.CardResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
public interface CardService {

    Long createCard(CardCreateDTO cardCreateDTO);
    List<CardResponseDTO> getCards(Long sparkUserId);
    CardResponseDTO getCard(Long id);
    Map<String,Long> modifyCard(Long id, CardCreateDTO cardCreateDTO);
    Map<String,Long> deleteCard(Long id);
}
