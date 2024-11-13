package mutsa.yewon.talksparkbe.domain.guestBook.service;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.room.GuestBookRoomListDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.room.GuestBookRoomListResponse;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBook;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRepository;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GuestBookRoomService {

    private final RoomRepository roomRepository;

    public GuestBookRoomListResponse getGuestBookRoomList(String search, String sortBy) {

        List<Room> rooms = roomRepository.findAll();

        if (search != null && !search.isEmpty()){
            rooms = rooms.stream()
                    .filter(room -> room.getRoomName().contains(search) ||
                            room.getRoomParticipates().stream()
                                    .anyMatch(participant -> participant.getSparkUser().getName().contains(search))) // 참가자 이름으로 필터링
                    .toList();
        }

            switch (sortBy == null || sortBy.isEmpty() ? "latest" : sortBy) {
                case "latest" -> rooms.stream()
                        .sorted(Comparator.comparing(Room::getCreatedAt).reversed())
                        .toList();
                case "alphabetical" -> rooms.stream()
                        .sorted(Comparator.comparing(Room::getRoomName))
                        .toList();
            }

        //TODO: isFavorited 추가되면 구현
//        } else if ("favorites".equalsIgnoreCase(sortBy)) {
//            rooms = rooms.stream()
//                    .filter(Room::isFavorited)
//                    .collect(Collectors.toList());
//        }

        List<GuestBookRoomListDTO> guestBookRoomListDTO = rooms.stream()
                .map(room -> GuestBookRoomListDTO.builder()
                        .roomId(room.getRoomId())
                        .roomName(room.getRoomName())
                        .roomDateTime(room.getCreatedAt())
                        .roomPeopleCount((long) room.getRoomParticipates().size())
                        .preViewContent(getLastGuestBookContent(room))
                        .build())
                .toList();


        return GuestBookRoomListResponse.builder()
                .roomGuestBookCount((long) rooms.size())
                .roomGuestBook(guestBookRoomListDTO)
                .build();
    }

    // 마지막 방명록 대화 가져오기
    public static String getLastGuestBookContent(Room room) {
        return room.getGuestBooks().stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(GuestBook::getGuestBookDateTime))
                .map(GuestBook::getGuestBookContent)
                .orElse("방명록이 없습니다."); // 방명록이 없는 경우 null값 반환
    }
}
