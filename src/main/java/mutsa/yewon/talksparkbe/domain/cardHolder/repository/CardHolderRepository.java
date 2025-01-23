package mutsa.yewon.talksparkbe.domain.cardHolder.repository;

import mutsa.yewon.talksparkbe.domain.cardHolder.entity.CardHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardHolderRepository extends JpaRepository<CardHolder, Long> {

    @Query("select c from CardHolder c where c.sparkUser.id = :sparkUserId order by c.id DESC ")
    List<CardHolder> findBySparkUserId(@Param("sparkUserId") Long sparkUserId);

    @Query("select c from CardHolder c where c.sparkUser.id = :sparkUserId order by c.name ASC ")
    List<CardHolder> getCardHolderByAlphabet(@Param("sparkUserId") Long sparkUserId);

    @Query("select c from CardHolder c where c.sparkUser.id = :sparkUserId and c.bookMark = true")
    List<CardHolder> getCardHolderByBookMark(@Param("sparkUserId") Long sparkUserId);

    int countBySparkUserId(Long sparkUserId);

    @Query("select c from CardHolder c where c.name = :name")
    List<CardHolder> getCardHoldersByName(@Param("name") String name);
}
