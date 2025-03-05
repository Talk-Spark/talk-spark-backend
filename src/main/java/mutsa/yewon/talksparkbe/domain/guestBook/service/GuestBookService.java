package mutsa.yewon.talksparkbe.domain.guestBook.service;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.card.entity.CardThema;
import mutsa.yewon.talksparkbe.domain.card.repository.CardRepository;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.*;
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
import mutsa.yewon.talksparkbe.global.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class GuestBookService {

    private final SparkUserRepository sparkUserRepository;
    private final GuestBookRepository guestBookRepository;
    private final GuestBookRoomRepository guestBookRoomRepository;
    private final GuestBookRoomSparkUserRepository guestBookRoomSparkUserRepository;
    private final CardRepository cardRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public void createGuestBookData(Long roomId) {
        SparkUser sparkUser = sparkUserRepository.findById(SecurityUtil.getLoggedInUserId()).orElseThrow(()
                -> new CustomTalkSparkException(ErrorCode.USER_NOT_EXIST));

        Room room = roomRepository.findById(roomId).orElseThrow(()
                -> new CustomTalkSparkException(ErrorCode.ROOM_NOT_FOUND));

        GuestBookRoom guestBookRoom = GuestBookRoom.builder()
                .room(room)
                .build();
        guestBookRoomRepository.save(guestBookRoom);

        for(RoomParticipate roomParticipate : room.getRoomParticipates()) {
            GuestBookRoomSparkUser guestBookRoomSparkUser = GuestBookRoomSparkUser.builder()
                    .guestBookRoom(guestBookRoom)
                    .sparkUser(roomParticipate.getSparkUser())
                    .build();
            guestBookRoomSparkUserRepository.save(guestBookRoomSparkUser);
        }
    }
    @Transactional
    public GuestBook createGuestBook(GuestBookPostRequestDTO guestBookPostRequestDTO, Boolean anonymity) {
        if (anonymity == null) {
            anonymity = false;
        }

        GuestBookRoom guestBookRoom = guestBookRoomRepository
                .findByRoomId(guestBookPostRequestDTO.getRoomId());

        SparkUser sparkUser = sparkUserRepository.findById(guestBookPostRequestDTO.getSparkUserId())
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.USER_NOT_EXIST));

        GuestBook guestBook = GuestBook.builder()
                .sparkUser(sparkUser)
                .guestBookContent(guestBookPostRequestDTO.getContent())
                .anonymity(anonymity)
                .build();
        guestBookRepository.save(guestBook);

        guestBookRoom.addGuestBooks(guestBook);


//        if(guestBookRoomSparkUserRepository
//                .findByGuestBookRoomIdAndSparkUserId(guestBookRoom.getGuestBookRoomId(), sparkUser.getId())
//                .isEmpty()) {
//            GuestBookRoomSparkUser guestBookRoomSparkUser = new GuestBookRoomSparkUser(guestBookRoom, sparkUser);
//            guestBookRoomSparkUserRepository.save(guestBookRoomSparkUser);
//            guestBookRoom.getGuestBookRoomSparkUsers().add(guestBookRoomSparkUser);
//        }

        return guestBook;
    }

    @Transactional
    public GuestBookListResponse getGuestBookList(GuestBookListRequestDTO guestBookListRequestDTO) {

        GuestBookRoomSparkUser guestBookRoomSparkUser = guestBookRoomSparkUserRepository
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
                .isGuestBookFavorited(guestBookRoomSparkUser.getIsGuestBookFavorited())
                .guestBookData(createGuestBookListResponse(guestBookRoom.getGuestBookRoomId(),guestBookRoom.getGuestBooks(), sparkUser.getId()))
                .build();

        }

        //TODO: Room-Card로 변경하기
        public List<GuestBookListDTO> createGuestBookListResponse(Long guestBookRoomId, List<GuestBook> guestBooks, Long sparkUserId) {
            List<Card> cards = cardRepository.findBySparkUserId(sparkUserId);
            Card card = cards.isEmpty() ? null : cards.get(0);

            return guestBooks.stream()
                    .map(guestBook -> GuestBookListDTO.builder()
                            .guestBookId(guestBook.getGuestBookId())
                            .sparkUserName(guestBook.isAnonymity() ? "익명" : guestBook.getSparkUser().getName()) // 익명 처리
                            .guestBookContent(guestBook.getGuestBookContent())
                            .guestBookDateTime(guestBook.getGuestBookDateTime())
                            .isOwnerGuestBook(guestBook.getSparkUser().getId().equals(sparkUserId)) // 작성자 확인
                            .cardThema(guestBook.isAnonymity() ?
                                    CardThema.values()[new Random().nextInt(CardThema.values().length)]:
                                    Objects.requireNonNull(card).getCardThema())
                            .build())
                    .collect(Collectors.toList());
        }

    public RoomIdResponseDTO getRoomId(Long sparkUserId) {
        GuestBookRoomSparkUser latestGuestBookRoomSparkUser = guestBookRoomSparkUserRepository
                .findTopBySparkUserIdOrderByGuestBookRoomSparkUserDateTimeDesc(sparkUserId)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.GUESTBOOK_ROOM_NOT_FOUND));

        return new RoomIdResponseDTO(latestGuestBookRoomSparkUser.getGuestBookRoom().getGuestBookRoomId());
    }
}
