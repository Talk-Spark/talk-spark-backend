package mutsa.yewon.talksparkbe.domain.cardHolder.repository;

import mutsa.yewon.talksparkbe.domain.cardHolder.entity.CardHolder;
import mutsa.yewon.talksparkbe.domain.cardHolder.entity.StoredCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoredCardRepository extends JpaRepository<StoredCard, Long> {

    @Query("select s from StoredCard s where s.cardHolder.id = :cardHolderId order by s.name ASC ")
    List<StoredCard> findByCardHolderId(@Param("cardHolderId") Long cardHolderId);
}
