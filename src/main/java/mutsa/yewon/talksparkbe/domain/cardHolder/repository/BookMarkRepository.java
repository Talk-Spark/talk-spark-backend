package mutsa.yewon.talksparkbe.domain.cardHolder.repository;

import mutsa.yewon.talksparkbe.domain.cardHolder.entity.BookMarks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookMarkRepository extends JpaRepository<BookMarks, Long> {
    Optional<BookMarks> findByCardIdAndSparkUserId(Long cardId, Long sparkUserId);

    List<BookMarks> findBySparkUserId(Long sparkUserId);
}
