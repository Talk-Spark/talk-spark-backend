package mutsa.yewon.talksparkbe.domain.card.repository;

import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
