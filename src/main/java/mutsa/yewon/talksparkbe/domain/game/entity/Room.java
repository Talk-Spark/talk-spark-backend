package mutsa.yewon.talksparkbe.domain.game.entity;

import jakarta.persistence.*;
import lombok.*;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBook;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBookRoom;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    private String roomName; // 방 이름

    private String code;

    private int maxPeople; // 최대 참가자 수

    private int difficulty;

    private Long hostId;

    private boolean isStarted = false;
    private boolean isFinished = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "room")
    private List<RoomParticipate> roomParticipates = new ArrayList<>();

    @OneToOne(mappedBy = "room",cascade = CascadeType.PERSIST)
    private GuestBookRoom guestBookRoom;

    @Builder
    private Room(String roomName, int maxPeople, int difficulty, Long hostId) {
        this.roomName = roomName;
        this.maxPeople = maxPeople;
        this.difficulty = difficulty;
        this.hostId = hostId;
    }

    public void addRoomParticipate(RoomParticipate roomParticipate) {
        this.roomParticipates.add(roomParticipate);
    }

    public void assignRoomParticipate(RoomParticipate roomParticipate) {
        this.addRoomParticipate(roomParticipate);
        roomParticipate.assignRoom(this);
    }

    public void start() {
        this.isStarted = true;
    }

    public void finish() {
        this.isFinished = true;
    }

}
