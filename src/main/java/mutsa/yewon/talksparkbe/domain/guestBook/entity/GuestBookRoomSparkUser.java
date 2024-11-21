package mutsa.yewon.talksparkbe.domain.guestBook.entity;

import jakarta.persistence.*;
import lombok.*;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class GuestBookRoomSparkUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestBookRoomSparkUserId;

    @ManyToOne
    @JoinColumn(name = "guestbook_room_id", nullable = false)
    private GuestBookRoom guestBookRoom;

    @ManyToOne
    @JoinColumn(name = "spark_user_id", nullable = false)
    private SparkUser sparkUser;

    private Boolean isGuestBookFavorited;

    @Builder
    public GuestBookRoomSparkUser(GuestBookRoom guestBookRoom, SparkUser sparkUser) {
        this.guestBookRoom = guestBookRoom;
        this.sparkUser = sparkUser;
        this.isGuestBookFavorited = false;
    }
}
