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
import org.springframework.security.core.context.SecurityContextHolder;
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

        SparkUser sparkUser = sparkUserRepository.findById(cardCreateDTO.getSparkUserId())
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.USER_NOT_EXIST));

        Card card = CardCreateDTO.toCard(cardCreateDTO, sparkUser);
        cardRepository.save(card);
        return card.getId();
    }

    @Override
    public List<CardResponseDTO> getCards(Long sparkUserId) {

        List<Card> cardList = cardRepository.findBySparkUserId(sparkUserId);

        if (cardList.isEmpty()) {
            throw new CustomTalkSparkException(ErrorCode.MUST_MAKE_CARD_FIRST);
        }

        List<CardResponseDTO> cardResponseDTOList = cardList.stream()
                .map(CardResponseDTO::fromCard).toList();

        return cardResponseDTOList;
    }

    @Override
    public CardResponseDTO getCard(Long id) {

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.CARD_NOT_EXIST));

        return CardResponseDTO.fromCard(card);
    }

    @Override
    public Map<String, Long> modifyCard(Long id, CardCreateDTO cardCreateDTO) {

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.CARD_NOT_EXIST));

        authorizeSparkUser(card);

        card.update(cardCreateDTO);
        cardRepository.save(card);

        return Map.of("UPDATED CARD", id);
    }

    @Override
    public Map<String, Long> deleteCard(Long id) {

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.CARD_NOT_EXIST));

        authorizeSparkUser(card);

        cardRepository.delete(card);

        return Map.of("DELETE COMPLETED", id);
    }

    private static void authorizeSparkUser(Card card){
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!card.getSparkUser().getKakaoId().equals(kakaoId)) {
            throw new CustomTalkSparkException(ErrorCode.NOT_YOUR_CARD);
        }
    }

}
