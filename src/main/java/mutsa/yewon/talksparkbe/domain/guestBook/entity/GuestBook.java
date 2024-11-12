package mutsa.yewon.talksparkbe.domain.guestBook.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GuestBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;

    @ManyToOne
    @JoinColumn(name = "spark_user_id")
    private SparkUser sparkUser;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private String guestBookContent;

    @CreatedDate
    private LocalDateTime guestBookDateTime;

}
