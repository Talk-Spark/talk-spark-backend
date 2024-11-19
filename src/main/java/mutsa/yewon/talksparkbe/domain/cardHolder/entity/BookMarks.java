package mutsa.yewon.talksparkbe.domain.cardHolder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookMarks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmarks_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "spark_user_id")
    private SparkUser sparkUser;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;
}
