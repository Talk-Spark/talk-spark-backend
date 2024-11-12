package mutsa.yewon.talksparkbe.domain.guestBook.entity;

import jakarta.persistence.*;
import lombok.*;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GuestBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestBookId;

    @ManyToOne
    @JoinColumn(name = "spark_user_id")
    private SparkUser sparkUser;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private String guestBookContent;

    @CreatedDate
    private LocalDateTime guestBookDateTime;

    @Builder
    public GuestBook(Long guestBookId, SparkUser sparkUser, Room room, String guestBookContent, LocalDateTime guestBookDateTime) {
        this.room = room;
        this.sparkUser = sparkUser;
        this.guestBookId = guestBookId;
        this.guestBookContent = guestBookContent;
        this.guestBookDateTime = guestBookDateTime;
    }

}
