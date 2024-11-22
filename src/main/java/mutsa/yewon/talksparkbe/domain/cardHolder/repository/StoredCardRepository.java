package mutsa.yewon.talksparkbe.domain.cardHolder.repository;

import mutsa.yewon.talksparkbe.domain.cardHolder.entity.CardHolder;
import mutsa.yewon.talksparkbe.domain.cardHolder.entity.StoredCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoredCardRepository extends JpaRepository<StoredCard, Long> {

    List<StoredCard> findByCardHolderId(Long cardHolderId);
}
