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
    @JoinColumn(name = "guestbook_room_id")
    private GuestBookRoom guestBookRoom;

    @ManyToOne
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
