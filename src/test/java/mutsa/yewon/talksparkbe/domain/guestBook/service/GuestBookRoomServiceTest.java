package mutsa.yewon.talksparkbe.domain.guestBook.service;

import jakarta.transaction.Transactional;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomParticipateRepository;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.room.GuestBookRoomListResponse;
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
class GuestBookRoomServiceTest {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private SparkUserRepository sparkUserRepository;
    @Autowired
    private GuestBookRepository guestBookRepository;
    @Autowired
    private RoomParticipateRepository roomParticipateRepository;
    @Autowired
    private GuestBookRoomService guestBookRoomService;


    @Test
    @Transactional
    @Rollback
    void getGuestBookRoomList() {
        SparkUser sparkUser1 = SparkUser.builder()
                .name("이윤정")
                .kakaoId("1234")
                .build();
        SparkUser sparkUser2 = SparkUser.builder()
                .name("김민우")
                .kakaoId("5678")
                .build();

        sparkUserRepository.save(sparkUser1);
        sparkUserRepository.save(sparkUser2);

        Room room1 = Room.builder()
                .roomName("테스트")
                .difficulty(2)
                .maxPeople(4)
                .build();
        Room room2 = Room.builder()
                .roomName("테스트2")
                .difficulty(2)
                .maxPeople(4)
                .build();

        roomRepository.save(room1);
        roomRepository.save(room2);

        RoomParticipate roomParticipate1 = RoomParticipate.builder()
                .room(room1)
                .sparkUser(sparkUser1)
                .isOwner(true)
                .build();
        RoomParticipate roomParticipate2 = RoomParticipate.builder()
                .room(room1)
                .sparkUser(sparkUser1)
                .isOwner(false)
                .build();
        roomParticipateRepository.save(roomParticipate1);
        roomParticipateRepository.save(roomParticipate2);
        room1.getRoomParticipates().add(roomParticipate1);
        room1.getRoomParticipates().add(roomParticipate2);

        GuestBook guestBook1 = GuestBook.builder()
                .guestBookContent("멋사최고")
                .sparkUser(sparkUser1)
                .room(room1)
                .build();
        GuestBook guestBook2 = GuestBook.builder()
                .guestBookContent("멋사최고2")
                .sparkUser(sparkUser2)
                .room(room1)
                .build();

        guestBookRepository.save(guestBook1);
        guestBookRepository.save(guestBook2);
        room1.getGuestBooks().add(guestBook1);
        room1.getGuestBooks().add(guestBook2);


        System.out.println(room1.getRoomParticipates());

        // 서비스 호출
        GuestBookRoomListResponse response = guestBookRoomService.getGuestBookRoomList("테스", "alphabetical");

        // 콘솔에 출력하여 검증
        System.out.println(response);


        assertEquals(2L, response.getRoomGuestBookCount());
        assertEquals("테스트", response.getRoomGuestBook().get(0).getRoomName());
        assertEquals("멋사최고2", response.getRoomGuestBook().get(0).getPreViewContent());
    }

}