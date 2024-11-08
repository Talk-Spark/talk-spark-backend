package mutsa.yewon.talksparkbe.domain.game.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    private String roomName; // 방 이름

    private String code;

    private int maxPeople; // 최대 참가자 수

    private int difficulty;

    private boolean isStarted = false;
    private boolean isFinished = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "room")
    private List<RoomParticipate> roomParticipates = new ArrayList<>();

    @Builder
    private Room(String roomName, int maxPeople, int difficulty) {
        this.roomName = roomName;
        this.maxPeople = maxPeople;
        this.difficulty = difficulty;
    }

    public void addRoomParticipate(RoomParticipate roomParticipate) {
        this.roomParticipates.add(roomParticipate);
    }

    public void assignRoomParticipate(RoomParticipate roomParticipate) {
        this.addRoomParticipate(roomParticipate);
        roomParticipate.assignRoom(this);
    }

}
