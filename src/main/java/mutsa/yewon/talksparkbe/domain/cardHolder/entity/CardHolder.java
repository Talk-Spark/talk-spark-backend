package mutsa.yewon.talksparkbe.domain.cardHolder.entity;

import jakarta.persistence.*;
import lombok.*;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CardHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cardholder_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spark_user_id")
    @Setter
    private SparkUser sparkUser;

    private String name; // 개별 명함이면 사용자 이름, 팀별 명함 모음이면 팀 이름으로 저장

    @ElementCollection
    @Builder.Default
    @BatchSize(size = 8)
    private List<String> teammates = new ArrayList<>(); // 개별 명함인 경우 사용자 이름 저장, 팀별 명함 모음이면 팀원들 이름 모두 저장

    @Builder.Default
    @OneToMany(mappedBy = "cardHolder", cascade = CascadeType.ALL)
    @BatchSize(size = 8)
    private List<StoredCard> storedCards = new ArrayList<>(); // 개별 명함인 경우 1개, 팀별 명함 모음인 경우 팀원 수 만큼

    @CreatedDate
    @Column(name = "stored_at")
    private LocalDateTime storedAt;

    @Enumerated(EnumType.STRING)
    private StoredStatus storedStatus; // 개별 명함인지, 팀별 명함 모음인지 구분하기 위함

    private boolean bookMark;

    private int numOfTeammates;

    public static CardHolder cardToIndCardHolder(String name, SparkUser sparkUser) {

        CardHolder cardHolder = CardHolder.builder()
                .sparkUser(sparkUser)
                .name(name)
                .storedStatus(StoredStatus.INDIVIDUAL)
                .bookMark(false)
                .numOfTeammates(1)
                .build();

        List<String> name1 = List.of(name);

        cardHolder.addTeammatesName(name1);

        cardHolder.addSparkUser(sparkUser);


        return cardHolder;
    }

    public static CardHolder cardToTeamCardHolder(String teamName, SparkUser sparkUser, List<Card> cards) {
        CardHolder cardHolder = CardHolder.builder()
                .sparkUser(sparkUser)
                .name(teamName)
                .storedStatus(StoredStatus.TEAM)
                .numOfTeammates(cards.size())
                .bookMark(false)
                .build();

        List<String> teammateNames = cards.stream().map(card -> card.getName()).sorted().toList();


        cardHolder.addTeammatesName(teammateNames);

        cardHolder.addSparkUser(sparkUser);

        return cardHolder;
    }

    private void addTeammatesName(List<String> teammatesNames){
        this.teammates.addAll(teammatesNames);
    }

    public void addSparkUser(SparkUser sparkUser){
        this.sparkUser = sparkUser;
        sparkUser.getCardHolders().add(this);
    }

    public void bookMarkCard(){
        this.bookMark = !this.bookMark;
    }

    public void addStoredCards(List<Card> cards) {
        for (Card card : cards) {
            StoredCard storedCard = StoredCard.cardTostore(this, card);
            storedCards.add(storedCard);
        }
    }
}
