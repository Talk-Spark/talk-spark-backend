package mutsa.yewon.talksparkbe.domain.guestBook.service;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.GuestBookPostRequestDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBook;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRepository;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
public class GuestBookService {

    private final GuestBookRepository guestBookRepository;
    private RoomRepository roomRepository;
    private SparkUserRepository sparkUserRepository;

    public GuestBook createGuestBook(GuestBookPostRequestDTO guestBookPostRequestDTO) {
        Room room = roomRepository.findById(guestBookPostRequestDTO.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        SparkUser sparkUser = sparkUserRepository.findById(guestBookPostRequestDTO.getSparkUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        GuestBook guestBook = GuestBook.builder()
                .room(room)
                .sparkUser(sparkUser)
                .guestBookContent(guestBookPostRequestDTO.getContent())
                .guestBookDateTime(LocalDateTime.now())
                .build();

        return guestBookRepository.save(guestBook);
    }
}
