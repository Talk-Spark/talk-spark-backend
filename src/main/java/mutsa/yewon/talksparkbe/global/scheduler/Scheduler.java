package mutsa.yewon.talksparkbe.global.scheduler;

import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBook;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBookRoom;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBookRoomSparkUser;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRoomSparkUserRepository;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class Scheduler {

    //TODO: 방 데이터 삭제+명함 데이터 삭제 추가
    private final GuestBookRoomRepository guestBookRoomRepository;
    private final SparkUserRepository sparkUserRepository;
    private final GuestBookRepository guestBookRepository;
    private final GuestBookRoomSparkUserRepository guestBookRoomSparkUserRepository;
    private final RoomRepository roomRepository;


    public Scheduler(GuestBookRoomRepository  guestBookRoomRepository, SparkUserRepository sparkUserRepository, GuestBookRepository guestBookRepository, GuestBookRoomSparkUserRepository guestBookRoomSparkUserRepository, RoomRepository roomRepository) {
        this.guestBookRoomRepository = guestBookRoomRepository;
        this.sparkUserRepository = sparkUserRepository;
        this.guestBookRepository = guestBookRepository;
        this.guestBookRoomSparkUserRepository = guestBookRoomSparkUserRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void cleanUpData() {
        deleteGuestBooks();
        deleteRooms();
    }

    // guestBookRoom과 sparkUser 중간 테이블이 null값일 때 해당 guestBookRoom을 삭제함
    public void deleteGuestBooks() {
        List<GuestBookRoom> guestBookRooms = guestBookRoomRepository.findAll();
        for (GuestBookRoom guestBookRoom : guestBookRooms) {
            if(guestBookRoom.getGuestBookRoomSparkUsers() == null ||
                guestBookRoom.getGuestBookRoomSparkUsers().isEmpty()) {
                List<GuestBook> guestBooks = guestBookRoom.getGuestBooks();
                guestBookRepository.deleteAll(guestBooks);
                guestBookRoomRepository.delete(guestBookRoom);
            }
        }
    }

    public void deleteRooms() {
        List<Room> rooms = roomRepository.findByIsFinishedTrue();
        for (Room room : rooms) {
            if (!guestBookRoomRepository.existsByRoom(room)) {
                roomRepository.delete(room);
            }
        }

    }

}
