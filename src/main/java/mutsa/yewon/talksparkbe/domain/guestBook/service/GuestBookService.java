package mutsa.yewon.talksparkbe.domain.guestBook.service;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookListDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookListRequest;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookPostRequestDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookListResponse;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBook;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRepository;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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

    public GuestBookListResponse getGuestBookList(GuestBookListRequest guestBookListRequest) {
        Room room = roomRepository.findById(guestBookListRequest.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        SparkUser sparkUser = sparkUserRepository.findById(guestBookListRequest.getSparkUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<GuestBook> guestBooks = guestBookRepository.findById(room.getRoomId());

        return GuestBookListResponse.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .roomDateTime(room.getCreatedAt())
//              .guestBookFavorited(room.isFavorited())
                .guestBookData(createGuestBookListResponse(guestBooks, sparkUser.getKakaoId()))
                .build();

        }

        public static List<GuestBookListDTO> createGuestBookListResponse(Optional <GuestBook> guestBooks, String sparkUserKakaoId) {
            return guestBooks.stream()
                    .map(guestBook -> GuestBookListDTO.builder()
                            .guestBookId(guestBook.getGuestBookId())
                            .sparkUserName(guestBook.getSparkUser().getName())
                            .guestBookContent(guestBook.getGuestBookContent())
                            .guestBookDate(guestBook.getGuestBookDateTime())
                            .isOwnerGuestBook(guestBook.getSparkUser().getKakaoId().equals(sparkUserKakaoId)) // 작성자 확인
                            .build())
                    .collect(Collectors.toList());
        }
    }
