package mutsa.yewon.talksparkbe.domain.sparkUser.repository;

import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SparkUserRepository extends JpaRepository<SparkUser, Long> {
}
