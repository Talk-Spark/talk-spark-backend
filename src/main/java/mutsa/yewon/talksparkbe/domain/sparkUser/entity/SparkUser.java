package mutsa.yewon.talksparkbe.domain.sparkUser.entity;

import jakarta.persistence.*;
import lombok.*;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.cardHolder.entity.CardHolder;
import mutsa.yewon.talksparkbe.domain.cardHolder.entity.StoredCard;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBook;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBookRoomSparkUser;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@ToString(exclude = "roles")
public class SparkUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spark_user_id")
    private Long id;

    private String kakaoId;

    private String name;

    private String password;

    @Builder.Default
    @OneToMany(mappedBy = "sparkUser", cascade = CascadeType.ALL)
    private List<Card> cards = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "sparkUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardHolder> cardHolders = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "sparkUser", cascade = CascadeType.ALL)
    private List<RoomParticipate> roomParticipates = new ArrayList<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    private List<SparkUserRole> roles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "sparkUser" , cascade = CascadeType.ALL)
    private List<GuestBookRoomSparkUser> guestBookRoomSparkUsers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "sparkUser")
    private List<GuestBook> guestBooks = new ArrayList<>();

    public void addGuestBook(GuestBook guestBook){
        guestBooks.add(guestBook);
        guestBook.setSparkUser(this);
    }

    public void addCardHolder(CardHolder cardHolder){
        cardHolders.add(cardHolder);
        cardHolder.setSparkUser(this);
    }

    @PreRemove
    private void preRemove() {
        guestBooks.forEach(guestBook -> guestBook.setSparkUser(null));
    }

    public void addMemberRole(SparkUserRole memberRole) {
        roles.add(memberRole);
    }

    public void addGuestBookUser(GuestBookRoomSparkUser guestBookUser) {
        guestBookUser.setSparkUser(this);
        guestBookRoomSparkUsers.add(guestBookUser);
    }

    public void clearMemberRoleList() {
        roles.clear();
    }

}
