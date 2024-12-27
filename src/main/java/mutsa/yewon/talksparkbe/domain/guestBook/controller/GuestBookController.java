package mutsa.yewon.talksparkbe.domain.guestBook.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.guestBook.GuestBookControllerDocs;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookContent;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookListRequestDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookPostRequestDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.guestBook.GuestBookListResponse;
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

    private final GuestBookService guestBookService;
    private final GuestBookRoomService guestBookRoomService;
    private final SecurityUtil securityUtil;


    //TODO: RuntimeException("User not found") 에러코드로 리펙토링

    @PostMapping("/{roomId}")
    public ResponseEntity<?> postGuestBook(@PathVariable("roomId") Long roomId,
                                           @RequestParam(required = false) boolean anonymity,
                                           @Valid @RequestBody GuestBookContent content) {

        try {
            GuestBookPostRequestDTO guestBookPostRequestDTO = new GuestBookPostRequestDTO(securityUtil.getLoggedInUserId(),roomId, content);
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
            GuestBookListRequestDTO guestBookListRequestDTO = new GuestBookListRequestDTO(roomId, securityUtil.getLoggedInUserId());
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
            GuestBookRoomListResponse guestBookRoomListResponse = guestBookRoomService.getGuestBookRoomList(securityUtil.getLoggedInUserId(),search,sortBy);
            ResponseDTO<?> responseDTO = ResponseDTO.ok("방명록 방들이 조회되었습니다.", guestBookRoomListResponse);
            return ResponseEntity.status(200).body(responseDTO);
        } catch (IllegalArgumentException e) {
            throw new CustomTalkSparkException(ErrorCode.INVALID_FORMAT);
        }

    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteGuestBookRoom(@PathVariable("roomId") Long roomId) {

        try {
            guestBookRoomService.deleteGuestBookRoom(securityUtil.getLoggedInUserId(), roomId);
            ResponseDTO<?> responseDTO = ResponseDTO.ok("방명록 방이 삭제되었습니다.");
            return ResponseEntity.status(200).body(responseDTO);
        } catch (IllegalArgumentException e) {
            throw new CustomTalkSparkException(ErrorCode.INVALID_FORMAT);
        }

    }

    @PutMapping("/{roomId}")
    public ResponseEntity<?> UpdateGuestBookRoomFavorites(@PathVariable("roomId") Long roomId){

        try {
            guestBookRoomService.updateGuestBookRoomFavorites(securityUtil.getLoggedInUserId(), roomId);
            ResponseDTO<?> responseDTO = ResponseDTO.ok("방명록 방 즐겨찾기가 수정되었습니다.");
            return ResponseEntity.status(200).body(responseDTO);
        } catch (IllegalArgumentException e) {
            throw new CustomTalkSparkException(ErrorCode.INVALID_FORMAT);
        }
    }


}
