package mutsa.yewon.talksparkbe.domain.game.entity;

import jakarta.persistence.*;
import lombok.*;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class RoomParticipate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomParticipateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spark_user_id")
    private SparkUser sparkUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    private boolean isOwner;

    @Builder
    private RoomParticipate(SparkUser sparkUser, Room room, boolean isOwner) {
        this.sparkUser = sparkUser;
        this.room = room;
        this.isOwner = isOwner;
    }

    public void assignRoom(Room room) {
        this.room = room;
    }

    public void addSparkUser(SparkUser sparkUser) {
        this.sparkUser = sparkUser;
        sparkUser.getRoomParticipates().add(this);
    }

}
