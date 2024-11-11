package mutsa.yewon.talksparkbe.domain.card.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.domain.card.dto.CardCreateDTO;
import mutsa.yewon.talksparkbe.domain.card.dto.CardResponseDTO;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.card.repository.CardRepository;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final SparkUserRepository sparkUserRepository;

    @Override
    public Long createCard(CardCreateDTO cardCreateDTO) {

        SparkUser sparkUser = sparkUserRepository.findByKakaoId(cardCreateDTO.getKakaoId())
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.USER_NOT_EXIST));

        Card card = CardCreateDTO.toCard(cardCreateDTO, sparkUser);
        cardRepository.save(card);
        return card.getId();
    }

    @Override
    public List<CardResponseDTO> getCards(String kakaoId) {
        return List.of();
    }

    @Override
    public CardResponseDTO getCard(Long id) {
        return null;
    }

    @Override
    public Map<String, String> modifyCard(CardCreateDTO cardCreateDTO) {
        return Map.of();
    }

    @Override
    public Map<String, String> deleteCard(Long id) {
        return Map.of();
    }
}
