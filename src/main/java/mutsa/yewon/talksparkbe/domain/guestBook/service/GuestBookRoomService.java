package mutsa.yewon.talksparkbe.domain.guestBook.service;

import ch.qos.logback.core.spi.ErrorCodes;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.room.GuestBookRoomListDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.room.GuestBookRoomListResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static mutsa.yewon.talksparkbe.global.exception.ErrorCode.ROOM_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class GuestBookRoomService {

    private final RoomRepository roomRepository;
    private final GuestBookRoomRepository guestBookRoomRepository;
    private final GuestBookRoomSparkUserRepository guestBookRoomSparkUserRepository;

    @Transactional
    public GuestBookRoomListResponse getGuestBookRoomList(String kakaoId, String search, String sortBy) {

        List<GuestBookRoom> guestBookRooms = guestBookRoomRepository
                .findRoomsBySparkUser(kakaoId);


        if (search != null && !search.isEmpty()) {
            guestBookRooms = guestBookRooms.stream()
                    .filter(guestBookRoom ->
                            guestBookRoom.getRoom().getRoomName().contains(search) ||
                                    guestBookRoom.getGuestBookRoomSparkUsers().stream()
                                            .anyMatch(guestBookRoomSparkUser ->
                                                    guestBookRoomSparkUser.getSparkUser()
                                                            .getName().contains(search))
                    )
                    .toList();
        }

        switch (sortBy == null || sortBy.isEmpty() ? "latest" : sortBy) {
            case "latest" ->
                    guestBookRooms = guestBookRooms.stream()
                            .sorted(Comparator.comparing(guestBookRoom -> guestBookRoom.getRoom().getCreatedAt(), Comparator.reverseOrder()))
                            .toList();
            case "alphabetical" ->
                    guestBookRooms = guestBookRooms.stream()
                            .sorted(Comparator.comparing(guestBookRoom -> guestBookRoom.getRoom().getRoomName()))
                            .toList();
            case "isFavorited" ->
                    guestBookRooms = guestBookRooms.stream()
                            .filter(guestBookRoom -> guestBookRoom.getIsGuestBookFavorited().equals(true))
                            .toList();
        }

        List<GuestBookRoomListDTO> guestBookRoomListDTO = guestBookRooms.stream()
                .map(guestBookRoom -> GuestBookRoomListDTO.builder()
                        .roomId(guestBookRoom.getRoom().getRoomId())
                        .roomName(guestBookRoom.getRoom().getRoomName())
                        .roomDateTime(guestBookRoom.getRoom().getCreatedAt())
                        .roomPeopleCount((long) guestBookRoom.getRoom().getRoomParticipates().size())
                        .preViewContent(getLastGuestBookContent(guestBookRoom))
                        .build())
                .toList();


        return GuestBookRoomListResponse.builder()
                .guestBookRoomCount((long) guestBookRooms.size())
                .guestBookRooms(guestBookRoomListDTO)
                .build();
    }

    // 마지막 방명록 대화 가져오기
    public static String getLastGuestBookContent(GuestBookRoom guestBookRoom) {
        return guestBookRoom
                .getGuestBooks().stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(GuestBook::getGuestBookDateTime))
                .map(GuestBook::getGuestBookContent)
                .orElse(null);
    }

    @Transactional
    public void deleteGuestBookRoom(String kakaoId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.ROOM_NOT_FOUND));

        GuestBookRoom guestBookRoom = room.getGuestBookRoom();

        GuestBookRoomSparkUser sparkUserToDelete = guestBookRoom.getGuestBookRoomSparkUsers().stream()
                .filter(deleteSparkUser -> deleteSparkUser.getSparkUser().getKakaoId().equals(kakaoId))
                .filter(deleteGuestBookRoom -> deleteGuestBookRoom.getGuestBookRoom().getRoom().getRoomId().equals(roomId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));


        guestBookRoom.getGuestBookRoomSparkUsers().remove(sparkUserToDelete);

        guestBookRoomSparkUserRepository.delete(sparkUserToDelete);

        guestBookRoomRepository.save(guestBookRoom);
    }
}
