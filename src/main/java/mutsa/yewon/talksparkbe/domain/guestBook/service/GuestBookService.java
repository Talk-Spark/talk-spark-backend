package mutsa.yewon.talksparkbe.domain.guestBook.service;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookListDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookListRequestDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookPostRequestDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookListResponse;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBook;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBookRoom;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBookRoomSparkUser;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRoomSparkUserRepository;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class GuestBookService {

    private final SparkUserRepository sparkUserRepository;
    private final GuestBookRepository guestBookRepository;
    private final GuestBookRoomRepository guestBookRoomRepository;
    private final GuestBookRoomSparkUserRepository guestBookRoomSparkUserRepository;

    //TODO: RuntimeException("User not found")) customException으로 수정
    @Transactional
    public GuestBook createGuestBook(GuestBookPostRequestDTO guestBookPostRequestDTO) {
        SparkUser sparkUser = sparkUserRepository
                .findById(guestBookPostRequestDTO.getSparkUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        GuestBookRoom guestBookRoom = guestBookRoomRepository
                .findByRoomId(guestBookPostRequestDTO.getRoomId());

        GuestBook guestBook = GuestBook.builder()
                .sparkUser(sparkUser)
                .guestBookContent(guestBookPostRequestDTO.getContent())
                .build();
        guestBookRepository.save(guestBook);

        guestBookRoom.addGuestBooks(guestBook);

        GuestBookRoomSparkUser guestBookRoomSparkUser = GuestBookRoomSparkUser.builder()
                .guestBookRoom(guestBookRoom)
                .sparkUser(sparkUser)
                .build();
        guestBookRoom.getGuestBookRoomSparkUsers().add(guestBookRoomSparkUser);

        return guestBook;
    }

    @Transactional
    public GuestBookListResponse getGuestBookList(GuestBookListRequestDTO guestBookListRequestDTO) {

        GuestBookRoomSparkUser guestBookRoomSparkUser = (GuestBookRoomSparkUser) guestBookRoomSparkUserRepository
                .findByGuestBookRoomIdAndSparkUserId(guestBookListRequestDTO.getRoomId(), guestBookListRequestDTO.getSparkUserId())
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.GUESTBOOK_ROOM_NOT_FOUND));

        GuestBookRoom guestBookRoom = guestBookRoomRepository
                .findByRoomId(guestBookListRequestDTO.getRoomId());

        SparkUser sparkUser = sparkUserRepository.findById(guestBookListRequestDTO.getSparkUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        return GuestBookListResponse.builder()
                .roomId(guestBookRoom.getRoom().getRoomId())
                .roomName(guestBookRoom.getRoom().getRoomName())
                .roomDateTime(guestBookRoom.getRoom().getCreatedAt())
                .isGuestBookFavorited(guestBookRoom.getIsGuestBookFavorited())
                .guestBookData(createGuestBookListResponse(guestBookRoom.getGuestBooks(), sparkUser.getKakaoId()))
                .build();

        }

        public static List<GuestBookListDTO> createGuestBookListResponse(List<GuestBook> guestBooks, String sparkUserKakaoId) {
            return guestBooks.stream()
                    .map(guestBook -> GuestBookListDTO.builder()
                            .guestBookId(guestBook.getGuestBookId())
                            .sparkUserName(guestBook.getSparkUser().getName())
                            .guestBookContent(guestBook.getGuestBookContent())
                            .guestBookDateTime(guestBook.getGuestBookDateTime())
                            .isOwnerGuestBook(guestBook.getSparkUser().getKakaoId().equals(sparkUserKakaoId)) // 작성자 확인
                            .build())
                    .collect(Collectors.toList());
        }
    }
