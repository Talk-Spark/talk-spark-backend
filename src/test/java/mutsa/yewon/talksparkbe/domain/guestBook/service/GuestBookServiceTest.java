package mutsa.yewon.talksparkbe.domain.guestBook.service;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomParticipateRepository;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBookRoom;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBookRoomSparkUser;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRoomSparkUserRepository;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class GuestBookServiceTest {

    @Autowired
    private GuestBookService guestBookService;

    @Autowired
    private GuestBookRoomService guestBookRoomService;

    @Autowired
    private SparkUserRepository sparkUserRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomParticipateRepository roomParticipateRepository;

    @Autowired
    private GuestBookRoomRepository guestBookRoomRepository;

    @Autowired
    private GuestBookRoomSparkUserRepository guestBookRoomSparkUserRepository;

    @DisplayName("")
    @Test
    void createGuestBookData(){

        //given
        SparkUser sparkUser1 = SparkUser.builder()
                .name("박승범")
                .build();

        SparkUser sparkUser2 = SparkUser.builder()
                .name("김민우")
                .build();

        sparkUserRepository.saveAll(List.of(sparkUser1, sparkUser2));

        Room room = Room.builder()
                .roomName("멋사")
                .build();

        roomRepository.save(room);

        RoomParticipate roomParticipate1 = RoomParticipate.builder()
                .room(room)
                .sparkUser(sparkUser1)
                .build();

        RoomParticipate roomParticipate2 = RoomParticipate.builder()
                .room(room)
                .sparkUser(sparkUser2)
                .build();

        room.addRoomParticipate(roomParticipate1);
        room.addRoomParticipate(roomParticipate2);

        roomParticipateRepository.saveAll(List.of(roomParticipate1, roomParticipate2));

        //when
        guestBookService.createGuestBookData(room.getRoomId());

        GuestBookRoom guestBookRoom = guestBookRoomRepository.findByRoomId(room.getRoomId());

        List<GuestBookRoomSparkUser> users = guestBookRoomSparkUserRepository.findAll();

        //then
        assertThat(guestBookRoom).isNotNull();
        assertThat(guestBookRoom.getRoom()).isEqualTo(room);
        assertThat(users).hasSize(2)
                .extracting("sparkUser.name")
                .containsExactly("박승범", "김민우");

    }
}