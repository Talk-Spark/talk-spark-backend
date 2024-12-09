package mutsa.yewon.talksparkbe.global.scheduler;

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

@Service
public class Scheduler {

    //TODO: 방 데이터 삭제+명함 데이터 삭제 추가
    private final GuestBookRoomRepository guestBookRoomRepository;
    private final SparkUserRepository sparkUserRepository;
    private final GuestBookRepository guestBookRepository;
    private final GuestBookRoomSparkUserRepository guestBookRoomSparkUserRepository;

    public Scheduler(GuestBookRoomRepository  guestBookRoomRepository, SparkUserRepository sparkUserRepository, GuestBookRepository guestBookRepository, GuestBookRoomSparkUserRepository guestBookRoomSparkUserRepository) {
        this.guestBookRoomRepository = guestBookRoomRepository;
        this.sparkUserRepository = sparkUserRepository;
        this.guestBookRepository = guestBookRepository;
        this.guestBookRoomSparkUserRepository = guestBookRoomSparkUserRepository;
    }

    // guestBookRoom과 sparkUser 중간 테이블이 null값일 때 해당 guestBookRoom을 삭제함
    @Transactional
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
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

}
