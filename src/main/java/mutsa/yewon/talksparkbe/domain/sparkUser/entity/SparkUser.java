package mutsa.yewon.talksparkbe.domain.sparkUser.entity;

import jakarta.persistence.*;
import lombok.*;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.cardHolder.entity.CardHolder;
import mutsa.yewon.talksparkbe.domain.cardHolder.entity.StoredCard;
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

    @OneToMany(mappedBy = "sparkUser", cascade = CascadeType.ALL)
    private List<Card> cards;

    @OneToMany(mappedBy = "sparkUser", cascade = CascadeType.ALL)
    private List<CardHolder> cardHolders;

    @OneToMany(mappedBy = "sparkUser", cascade = CascadeType.ALL)
    private List<RoomParticipate> roomParticipates;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    private List<SparkUserRole> roles = new ArrayList<>();

    public void addMemberRole(SparkUserRole memberRole) {
        roles.add(memberRole);
    }

    public void clearMemberRoleList() {
        roles.clear();
    }

    @OneToMany(mappedBy = "sparkUser", cascade = CascadeType.ALL)
    private List<GuestBookRoomSparkUser> guestBookRoomSparkUsers = new ArrayList<>();

}
