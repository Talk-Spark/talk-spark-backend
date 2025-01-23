package mutsa.yewon.talksparkbe.domain.guestBook.entity;

import jakarta.persistence.*;
import lombok.*;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class GuestBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestBookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spark_user_id", nullable = true)
    @Setter
    private SparkUser sparkUser;

    private String guestBookContent;

    @CreatedDate
    private LocalDateTime guestBookDateTime;

    private boolean anonymity;

    @Builder
    public GuestBook(SparkUser sparkUser, String guestBookContent, boolean anonymity) {
        this.sparkUser = sparkUser;
        this.guestBookContent = guestBookContent;
        this.guestBookDateTime = LocalDateTime.now();
        this.anonymity = anonymity;
    }

    @Setter
    @ManyToOne(cascade = CascadeType.REMOVE) //guestBookRoom삭제 시 같이 삭제됨
    @JoinColumn(name = "guestbook_room_id")
    private GuestBookRoom guestBookRoom;

}
