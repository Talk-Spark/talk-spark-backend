package mutsa.yewon.talksparkbe.domain.guestBook.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GuestBookRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestBookRoomId;

    @OneToMany(mappedBy = "guestBookRoom", cascade = CascadeType.PERSIST)
    private List<GuestBookRoomSparkUser> guestBookRoomSparkUsers
            = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToMany(mappedBy = "guestBookRoom", cascade = CascadeType.PERSIST)
    private List<GuestBook> guestBooks = new ArrayList<>();

    @Builder
    public GuestBookRoom(Room room,
                         List<GuestBookRoomSparkUser> guestBookRoomSparkUsers,
                         List<GuestBook> guestBooks) {
        this.room = room;
        this.guestBookRoomSparkUsers = guestBookRoomSparkUsers;
        this.guestBooks = guestBooks;
    }

    public GuestBookRoom(Room room) {
        this.room = room;
    }

    public void addGuestBooks(GuestBook guestBook) {
        this.guestBooks.add(guestBook);
        guestBook.setGuestBookRoom(this);
    }

}
