package mutsa.yewon.talksparkbe.domain.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String major;

    private String mbti;

    private String hobby;

    private String lookAlike;

    private String slogan;

    private String tmi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spark_user_id")
    private SparkUser sparkUser;
}
