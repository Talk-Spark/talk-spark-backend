package mutsa.yewon.talksparkbe.domain.sparkUser.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SparkUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sparkUserId;

    private String name;

    private String kakaoId;

    @Builder
    private SparkUser(String name, String kakaoId) {
        this.name = name;
        this.kakaoId = kakaoId;
    }

}
