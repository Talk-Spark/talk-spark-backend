package mutsa.yewon.talksparkbe.domain.cardHolder.service;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.card.repository.CardRepository;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.*;
import mutsa.yewon.talksparkbe.domain.cardHolder.entity.CardHolder;
import mutsa.yewon.talksparkbe.domain.cardHolder.entity.StoredCard;
import mutsa.yewon.talksparkbe.domain.cardHolder.repository.CardHolderRepository;
import mutsa.yewon.talksparkbe.domain.cardHolder.repository.StoredCardRepository;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StoredCardServiceImpl implements StoredCardService {

    //TODO : 동일 명함 저장 시 예외처리, 페이지네이션 적용, 반환 값 리팩토링

    private final CardHolderRepository cardHolderRepository;

    private final CardRepository cardRepository;

    private final SparkUserRepository sparkUserRepository;

    private final StoredCardRepository storedCardRepository;


    @Override
    @Transactional
    public Long storeIndCard(IndCardHolderCreateDTO indCardHolderCreateDTO) {
        SparkUser sparkUser = sparkUserRepository.findById(indCardHolderCreateDTO.getSparkUserId())
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.USER_NOT_EXIST));

        Card card = cardRepository.findById(indCardHolderCreateDTO.getCardId())
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.CARD_NOT_EXIST));

        CardHolder cardHolder = CardHolder.cardToIndCardHolder(card.getName(), sparkUser);

        StoredCard.cardTostore(cardHolder, card);

        cardHolderRepository.save(cardHolder);

        return cardHolder.getId();
    }

    @Override
    @Transactional
    public Long storeTeamCard(TeamCardHolderCreateDTO teamCardHolderCreateDTO) {
        SparkUser sparkUser = sparkUserRepository.findById(teamCardHolderCreateDTO.getSparkUserId())
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.USER_NOT_EXIST));

//        List<Card> cards = new ArrayList<>();
//
//        for (Long cardId : teamCardHolderCreateDTO.getCardIds()) {
//            Card card = cardRepository.findById(cardId)
//                    .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.CARD_NOT_EXIST));
//
//            cards.add(card);
//        }

        List<Card> cards = cardRepository.findAllById(teamCardHolderCreateDTO.getCardIds());

        CardHolder cardHolder =
                CardHolder.cardToTeamCardHolder(teamCardHolderCreateDTO.getTeamName(), sparkUser, cards);

//        for (Card card : cards) {
//            StoredCard.cardTostore(cardHolder, card);
//        }

        cardHolder.addStoredCards(cards);

        sparkUser.addCardHolder(cardHolder);

        cardHolderRepository.save(cardHolder);

        return cardHolder.getId();
    }

    @Override
    public List<StoredCardDTO> getCardHolderDTO(Long cardHolderId) {

        CardHolder cardHolder = cardHolderRepository.findById(cardHolderId)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.CARDHOLDER_NOT_EXIST));


//        List<StoredCard> storedCards = storedCardRepository.findByCardHolderId(cardHolderId);

        List<StoredCard> storedCards = cardHolder.getStoredCards();

        if (storedCards.isEmpty()) {
            throw new CustomTalkSparkException(ErrorCode.CARD_NOT_EXIST);
        }

        return storedCards.stream()
                .map(storedCard -> StoredCardDTO.entityToDTO(storedCard, cardHolder)).toList();
    }

    @Override
    public CardHolderListDTO getCardHolderDTOs(String searchType, Long sparkUserId) {

        int numOfCards = cardHolderRepository.countBySparkUserId(sparkUserId);

        List<CardHolder> cardHolders = new ArrayList<>();


        if (searchType.equals("Alphabet")) {
            cardHolders = cardHolderRepository.getCardHolderByAlphabet(sparkUserId);

            if (cardHolders.isEmpty()) {
                throw new CustomTalkSparkException(ErrorCode.CARDHOLDER_NOT_EXIST);
            }

        } else if (searchType.equals("Bookmark")) {
            cardHolders = cardHolderRepository.getCardHolderByBookMark(sparkUserId);

            if (cardHolders.isEmpty()) {
                throw new CustomTalkSparkException(ErrorCode.NO_BOOKMARKED_CONTENT);
            }

        } else {

            cardHolders = cardHolderRepository.findBySparkUserId(sparkUserId);

            if (cardHolders.isEmpty()) {
                throw new CustomTalkSparkException(ErrorCode.CARDHOLDER_NOT_EXIST);
            }


        }

        List<CardHolderDTO> cardHolderDTOS = cardHolders.stream()
                .map(CardHolderDTO::entitytoDTO).toList();

        return CardHolderListDTO.builder()
                .searchType(searchType)
                .numOfCards(numOfCards)
                .cardHolders(cardHolderDTOS)
                .build();
    }

    @Override
    @Transactional
    public Map<String, Long> bookMarkCard(Long cardHolderId) {
        CardHolder cardHolder = cardHolderRepository.findById(cardHolderId)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.CARDHOLDER_NOT_EXIST));

        cardHolder.bookMarkCard();

        cardHolderRepository.save(cardHolder);

        return Map.of("cardHolderId", cardHolder.getId());
    }

    @Override
    @Transactional
    public Map<String, Long> deleteCardHolder(Long cardHolderId) {
        CardHolder cardHolder = cardHolderRepository.findById(cardHolderId)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.CARDHOLDER_NOT_EXIST));

        cardHolderRepository.delete(cardHolder);

        return Map.of("cardHolderId", cardHolder.getId());
    }

    @Override
    public CardHolderListDTO getCardHolderByName(String searchType) {
        List<CardHolder> cardHolders = cardHolderRepository.getCardHoldersByName(searchType);

        if (cardHolders.isEmpty()) {
            throw new CustomTalkSparkException(ErrorCode.NO_MATCHING_CARDHOLDER);
        }

        List<CardHolderDTO> cardHolderDTOS = cardHolders.stream()
                .map(CardHolderDTO::entitytoDTO).toList();

        return CardHolderListDTO.builder()
                .searchType(searchType)
                .numOfCards(cardHolderDTOS.size())
                .cardHolders(cardHolderDTOS)
                .build();
    }

    @Override
    public List<MainPageCardHolderDTO> getMainPageCards(Long sparkUserId) {
        List<CardHolder> cardHolders = cardHolderRepository.findBySparkUserId(sparkUserId);

        if (cardHolders.isEmpty()) {
            throw new CustomTalkSparkException(ErrorCode.CARDHOLDER_NOT_EXIST);
        }

        return cardHolders.stream()
                .map(MainPageCardHolderDTO::entityToDTO).toList();
    }


}
