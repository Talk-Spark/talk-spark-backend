package mutsa.yewon.talksparkbe.domain.card.repository;

import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

//    @Query("select c from Card c where c.sparkUser.kakaoId = :sparkUserId")
//    List<Card> findBySparkUserId(@Param("sparkUserId") String sparkUserId);

    List<Card> findBySparkUserId(Long sparkUserId);

}
