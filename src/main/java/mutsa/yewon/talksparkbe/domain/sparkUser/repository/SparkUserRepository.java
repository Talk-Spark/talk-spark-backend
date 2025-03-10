package mutsa.yewon.talksparkbe.domain.sparkUser.repository;

import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface SparkUserRepository extends JpaRepository<SparkUser, Long> {
    Optional<SparkUser> findByKakaoId(String kakaoId);

    Optional<SparkUser> findByName(String name);

    @Query("select su " +
            "from SparkUser su " +
            "join fetch su.cards cl " +
            "where su.id = :sparkUserId")
    Optional<SparkUser> findByIdWithCards(Long sparkUserId);

}
