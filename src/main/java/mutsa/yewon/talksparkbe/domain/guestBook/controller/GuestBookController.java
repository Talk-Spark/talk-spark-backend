package mutsa.yewon.talksparkbe.domain.guestBook.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.GuestBookControllerDocs;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.*;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.room.GuestBookRoomListResponse;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBook;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.service.GuestBookRoomService;
import mutsa.yewon.talksparkbe.domain.guestBook.service.GuestBookService;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import mutsa.yewon.talksparkbe.global.dto.ResponseDTO;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
//import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import mutsa.yewon.talksparkbe.global.util.SecurityUtil;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping( "/api/guest-books")
@RestController
@RequiredArgsConstructor
public class GuestBookController implements GuestBookControllerDocs {

    private final RoomRepository roomRepository;
    private final GuestBookService guestBookService;
    private final GuestBookRoomService guestBookRoomService;

//    @PostMapping("/create")
//    public ResponseEntity<?> postGuestBook(@RequestParam(required = true) Long roomId) {
//
//        try {
//            Room room = roomRepository.findById(roomId).orElseThrow(
//                    () -> new CustomTalkSparkException(ErrorCode.ROOM_NOT_FOUND));
//            // 방명록 초기 데이터를 생성할 때는 항상 room의 게임이 끝나있어야 함.
//            if(!room.isFinished()) {
//                room.setFinished(true);
//                roomRepository.save(room);
//            }
//            guestBookService.createGuestBookData(roomId);
//            ResponseDTO<?> responseDTO = ResponseDTO.created("방명록 초기 데이터 생성하였습니다.");
//            return ResponseEntity.status(201).body(responseDTO);
//        } catch (IllegalArgumentException e) {
//            throw new CustomTalkSparkException(ErrorCode.INVALID_FORMAT);
//        }
//    }

    @GetMapping("/room-id")
    public ResponseEntity<?> getRoomId(){
        RoomIdResponseDTO roomIdResponseDTO = guestBookService.getRoomId(SecurityUtil.getLoggedInUserId());
        ResponseDTO<?> responseDTO = ResponseDTO.created("방명록 생성시간을 기준으로 게임 방 id가 조회되었습니다.",roomIdResponseDTO);
        return ResponseEntity.status(200).body(responseDTO);
    }

    @PostMapping("/{roomId}")
    public ResponseEntity<?> postGuestBook(@PathVariable("roomId") Long roomId,
                                           @RequestParam(required = false) boolean anonymity,
                                           @Valid @RequestBody GuestBookContent content) {

        try {
            GuestBookPostRequestDTO guestBookPostRequestDTO = new GuestBookPostRequestDTO(SecurityUtil.getLoggedInUserId(),roomId, content);
            GuestBook guestBook = guestBookService.createGuestBook(guestBookPostRequestDTO, anonymity);
            ResponseDTO<?> responseDTO = ResponseDTO.created("방명록 내용이 작성되었습니다.");
            return ResponseEntity.status(201).body(responseDTO);
        } catch (IllegalArgumentException e) {
            throw new CustomTalkSparkException(ErrorCode.INVALID_FORMAT);
        }
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getGuestBookList(@PathVariable("roomId") Long roomId) {

        try {
            GuestBookListRequestDTO guestBookListRequestDTO = new GuestBookListRequestDTO(roomId, SecurityUtil.getLoggedInUserId());
            GuestBookListResponse guestBookListResponse = guestBookService.getGuestBookList(guestBookListRequestDTO);
            ResponseDTO<?> responseDTO = ResponseDTO.ok("방명록 내용이 조회되었습니다.", guestBookListResponse);
            return ResponseEntity.status(200).body(responseDTO);
        } catch (IllegalArgumentException e) {
            throw new CustomTalkSparkException(ErrorCode.INVALID_FORMAT);
        }
    }

    @GetMapping
    public ResponseEntity<?> getGuestBookRoomList(@RequestParam(required = false) String search,
                                                  @RequestParam(required = false) String sortBy) {

        try {
            GuestBookRoomListResponse guestBookRoomListResponse = guestBookRoomService.getGuestBookRoomList(SecurityUtil.getLoggedInUserId(),search,sortBy);
            ResponseDTO<?> responseDTO = ResponseDTO.ok("방명록 방들이 조회되었습니다.", guestBookRoomListResponse);
            return ResponseEntity.status(200).body(responseDTO);
        } catch (IllegalArgumentException e) {
            throw new CustomTalkSparkException(ErrorCode.INVALID_FORMAT);
        }

    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteGuestBookRoom(@PathVariable("roomId") Long roomId) {

        try {
            guestBookRoomService.deleteGuestBookRoom(SecurityUtil.getLoggedInUserId(), roomId);
            ResponseDTO<?> responseDTO = ResponseDTO.ok("방명록 방이 삭제되었습니다.");
            return ResponseEntity.status(200).body(responseDTO);
        } catch (IllegalArgumentException e) {
            throw new CustomTalkSparkException(ErrorCode.INVALID_FORMAT);
        }

    }

    @PutMapping("/{roomId}")
    public ResponseEntity<?> UpdateGuestBookRoomFavorites(@PathVariable("roomId") Long roomId){

        try {
            guestBookRoomService.updateGuestBookRoomFavorites(SecurityUtil.getLoggedInUserId(), roomId);
            ResponseDTO<?> responseDTO = ResponseDTO.ok("방명록 방 즐겨찾기가 수정되었습니다.");
            return ResponseEntity.status(200).body(responseDTO);
        } catch (IllegalArgumentException e) {
            throw new CustomTalkSparkException(ErrorCode.INVALID_FORMAT);
        }
    }


}
