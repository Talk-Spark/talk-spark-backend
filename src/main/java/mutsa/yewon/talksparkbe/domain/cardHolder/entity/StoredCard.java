package mutsa.yewon.talksparkbe.domain.cardHolder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoredCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storedcard_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cardholder_id")
    private CardHolder cardHolder;

    private String name;

    private Integer age;

    private String major;

    private String mbti;

    private String hobby;

    private String lookAlike;

    private String slogan;

    private String tmi;

    public static StoredCard cardTostore(CardHolder cardHolder, Card card) {
        StoredCard storedCard = StoredCard.builder()
                .cardHolder(cardHolder)
                .name(card.getName())
                .age(card.getAge())
                .major(card.getMajor())
                .mbti(card.getMbti())
                .hobby(card.getHobby())
                .lookAlike(card.getLookAlike())
                .slogan(card.getSlogan())
                .tmi(card.getTmi())
                .build();

        storedCard.addStoredCard(cardHolder);

        return storedCard;
    }


    private void addStoredCard(CardHolder cardHolder) {
        cardHolder.getStoredCards().add(this);
    }

}
