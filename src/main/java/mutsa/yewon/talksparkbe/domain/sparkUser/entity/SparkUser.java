package mutsa.yewon.talksparkbe.domain.sparkUser.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class SparkUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sparkUserId;

    private String name;

    private String kakaoId;

}
