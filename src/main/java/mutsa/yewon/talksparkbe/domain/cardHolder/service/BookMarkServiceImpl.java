package mutsa.yewon.talksparkbe.domain.cardHolder.service;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.card.repository.CardRepository;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.BookMarkCreateDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.entity.BookMarks;
import mutsa.yewon.talksparkbe.domain.cardHolder.repository.BookMarkRepository;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookMarkServiceImpl implements BookMarkService {

    private final BookMarkRepository bookMarkRepository;

    private final CardRepository cardRepository;

    private final SparkUserRepository sparkUserRepository;

    @Override
    public Long createBookMark(BookMarkCreateDTO bookMarkCreateDTO) {
        Card card = cardRepository.findById(bookMarkCreateDTO.getCardId())
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.CARD_NOT_EXIST));

        SparkUser sparkUser = sparkUserRepository.findById(bookMarkCreateDTO.getSparkUserId())
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.USER_NOT_EXIST));

        Long presentBit = checkBookMark(card.getId(), sparkUser.getId());

        if(presentBit == 0L){
            BookMarks bookMarks = BookMarks.builder()
                    .card(card)
                    .sparkUser(sparkUser)
                    .build();

            bookMarkRepository.save(bookMarks);

            return bookMarks.getId();
        }

        bookMarkRepository.deleteById(presentBit);

        return presentBit;
    }

    private Long checkBookMark(Long cardId, Long sparkUserId) {
        Optional<BookMarks> bookmark = bookMarkRepository.findByCardIdAndSparkUserId(cardId, sparkUserId);

        if(bookmark.isEmpty()) {
            return 0L;
        }

        return bookmark.get().getId();
    }
}
