package mutsa.yewon.talksparkbe.domain.guestBook.entity;

import jakarta.persistence.*;
import lombok.*;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class GuestBookRoomSparkUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestBookRoomSparkUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_book_room_id")
    private GuestBookRoom guestBookRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spark_user_id")
    private SparkUser sparkUser;

    @Column(nullable = false)
    private Boolean isGuestBookFavorited;

    @PrePersist
    public void prePersist() {
        if (isGuestBookFavorited == null) {
            isGuestBookFavorited = false;
        }
    }

    @Builder
    public GuestBookRoomSparkUser(GuestBookRoom guestBookRoom, SparkUser sparkUser) {
        this.guestBookRoom = guestBookRoom;
        this.sparkUser = sparkUser;
        this.isGuestBookFavorited = false;
    }
}
