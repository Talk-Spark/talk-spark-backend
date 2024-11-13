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
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        SparkUser sparkUser1 = SparkUser.builder()
                .name("이윤정")
                .kakaoId("1234")
                .build();
        SparkUser sparkUser2 = SparkUser.builder()
                .name("김민우")
                .kakaoId("5678")
                .build();
        SparkUser sparkUser3 = SparkUser.builder()
                .name("박승범")
                .kakaoId("9101")
                .build();

        sparkUserRepository.save(sparkUser1);
        sparkUserRepository.save(sparkUser2);
        sparkUserRepository.save(sparkUser3);

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
                .sparkUser(sparkUser2)
                .isOwner(false)
                .build();
        RoomParticipate roomParticipate3 = RoomParticipate.builder()
                .room(room1)
                .sparkUser(sparkUser3)
                .isOwner(false)
                .build();
        RoomParticipate roomParticipate4 = RoomParticipate.builder()
                .room(room2)
                .sparkUser(sparkUser1)
                .isOwner(true)
                .build();
        roomParticipateRepository.save(roomParticipate1);
        roomParticipateRepository.save(roomParticipate2);
        roomParticipateRepository.save(roomParticipate3);
        roomParticipateRepository.save(roomParticipate4);
        room1.getRoomParticipates().add(roomParticipate1);
        room1.getRoomParticipates().add(roomParticipate2);
        room1.getRoomParticipates().add(roomParticipate3);
        room2.getRoomParticipates().add(roomParticipate4);

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
    }

    @Test
    @Transactional
    @Rollback
    void getGuestBookRoomList() {

        // 방 이름으로 검색
        GuestBookRoomListResponse roomResponseSortedByAlphabetical = guestBookRoomService
                .getGuestBookRoomList("1234","테스", "alphabetical");

        GuestBookRoomListResponse roomResponseSortedByDefault = guestBookRoomService
                .getGuestBookRoomList("1234","테스","");

        GuestBookRoomListResponse roomResponseSortedByLatest = guestBookRoomService
                .getGuestBookRoomList("1234","테스", "latest");
        System.out.println(roomResponseSortedByAlphabetical);
        System.out.println(roomResponseSortedByLatest);

        // 참가자 이름으로 검색
        GuestBookRoomListResponse userNameResponse = guestBookRoomService
                .getGuestBookRoomList("1234","김민", "alphabetical");
        System.out.println(userNameResponse);

        assertEquals(2L, roomResponseSortedByAlphabetical.getRoomGuestBookCount());
        assertEquals(1L, userNameResponse.getRoomGuestBookCount());
        assertEquals(roomResponseSortedByDefault.getRoomGuestBook().get(0).getRoomName(),
                roomResponseSortedByLatest.getRoomGuestBook().get(0).getRoomName());
        assertEquals("테스트", roomResponseSortedByAlphabetical.getRoomGuestBook().get(0).getRoomName());
        assertEquals("멋사최고2", roomResponseSortedByAlphabetical.getRoomGuestBook().get(0).getPreViewContent());
    }

}