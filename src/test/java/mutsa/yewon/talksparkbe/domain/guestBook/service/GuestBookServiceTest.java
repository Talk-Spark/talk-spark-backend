package mutsa.yewon.talksparkbe.domain.guestBook.service;

import jakarta.transaction.Transactional;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBook;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRepository;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GuestBookServiceTest {

    @Autowired
    GuestBookRepository guestBookRepository;
    @Autowired
    SparkUserRepository sparkUserRepository;
    @Autowired
    RoomRepository roomRepository;


    @Test
    @Transactional
    @Rollback
    void createGuestBook() {
        SparkUser sparkUser = SparkUser.builder()
                .name("이윤정")
                .kakaoId("dkkdkk")
                .build();
        Room room = Room.builder()
                .roomName("테스트")
                .difficulty(2)
                .maxPeople(4)
                .build();

        sparkUserRepository.save(sparkUser);
        roomRepository.save(room);

        GuestBook guestBook = GuestBook.builder()
                .guestBookContent("멋사최고")
                .sparkUser(sparkUser)
                .room(room)
                .build();

        guestBookRepository.save(guestBook);

        System.out.println("GuestBook ID: " + guestBook.getGuestBookId());
        System.out.println("GuestBook Content: " + guestBook.getGuestBookContent());
        System.out.println("SparkUser Name: " + guestBook.getSparkUser().getName());
        System.out.println("Room Name: " + guestBook.getRoom().getRoomName());

        assertNotNull(guestBook.getGuestBookId(), "GuestBook ID is null");
        assertEquals("멋사최고", guestBook.getGuestBookContent(),
                "Expected GuestBook Content: 멋사최고, but was: " + guestBook.getGuestBookContent());
        assertEquals("이윤정", guestBook.getSparkUser().getName(),
                "Expected SparkUser Name: 이윤정, but was: " + guestBook.getSparkUser().getName());
        assertEquals("테스트", guestBook.getRoom().getRoomName(),
                "Expected Room Name: 테스트, but was: " + guestBook.getRoom().getRoomName());
    }
}