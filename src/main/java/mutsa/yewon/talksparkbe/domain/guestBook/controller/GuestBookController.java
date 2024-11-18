package mutsa.yewon.talksparkbe.domain.guestBook.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping( "/guest-books")
@RestController
@RequiredArgsConstructor
public class GuestBookController {
    private final GuestBookService guestBookService;
    private final SparkUserRepository sparkUserRepository;
    private final GuestBookRepository guestBookRepository;
    private final GuestBookRoomService guestBookRoomService;


    //TODO: 카카오 인증 @RequestHeader("Authorization") String token 추가
    //TODO: RuntimeException("User not found") 에러코드로 리펙토링

    @PostMapping("/{roomId}")
    public ResponseEntity<?> PostGuestBook(@RequestParam String kakaoId,
                                           @PathVariable("roomId") Long roomId,
                                           @Valid @RequestBody GuestBookContent content) {

        SparkUser sparkUser = sparkUserRepository.findByKakaoId(kakaoId).orElseThrow(()
                -> new RuntimeException("User not found"));


        try {
            GuestBookPostRequestDTO guestBookPostRequestDTO = new GuestBookPostRequestDTO(roomId, sparkUser.getId(), content);
            GuestBook guestBook = guestBookService.createGuestBook(guestBookPostRequestDTO);
            ResponseDTO<?> responseDTO = ResponseDTO.created("작성되었습니다.");
            return ResponseEntity.status(201).body(responseDTO);
        } catch (IllegalArgumentException e) {
            throw new CustomTalkSparkException(ErrorCode.INVALID_FORMAT);
        }
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> GetGuestBookList(@RequestParam String kakaoId,
                                              @PathVariable("roomId") Long roomId) {

        SparkUser sparkUser = sparkUserRepository.findByKakaoId(kakaoId).orElseThrow(()
                -> new RuntimeException("User not found"));

        try {
            GuestBookListRequestDTO guestBookListRequestDTO = new GuestBookListRequestDTO(roomId, sparkUser.getId());
            GuestBookListResponse guestBookListResponse = guestBookService.getGuestBookList(guestBookListRequestDTO);
            ResponseDTO<?> responseDTO = ResponseDTO.ok("방명록 내용이 조회되었습니다.", guestBookListResponse);
            return ResponseEntity.status(200).body(responseDTO);
        } catch (IllegalArgumentException e) {
            throw new CustomTalkSparkException(ErrorCode.INVALID_FORMAT);
        }
    }

    @GetMapping
    public ResponseEntity<?> GetGuestBookRoomList(@RequestParam String kakaoId,
                                                  @RequestParam(required = false) String search,
                                                  @RequestParam(required = false) String sortBy) {

        SparkUser sparkUser = sparkUserRepository.findByKakaoId(kakaoId).orElseThrow(()
                -> new RuntimeException("User not found"));

        try {
            GuestBookRoomListResponse guestBookRoomListResponse = guestBookRoomService.getGuestBookRoomList(kakaoId,search,sortBy);
            ResponseDTO<?> responseDTO = ResponseDTO.ok("방명록 방들이 조회되었습니다.", guestBookRoomListResponse);
            return ResponseEntity.status(200).body(responseDTO);
        } catch (IllegalArgumentException e) {
            throw new CustomTalkSparkException(ErrorCode.INVALID_FORMAT);
        }

    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> DeleteGuestBookRoom(@RequestParam String kakaoId,
                                                 @PathVariable("roomId") Long roomId) {
        SparkUser sparkUser = sparkUserRepository.findByKakaoId(kakaoId).orElseThrow(()
                -> new RuntimeException("User not found"));

        try {
            guestBookRoomService.deleteGuestBookRoom(kakaoId, roomId);
            ResponseDTO<?> responseDTO = ResponseDTO.ok("방명록 방이 삭제되었습니다.");
            return ResponseEntity.status(200).body(responseDTO);
        } catch (IllegalArgumentException e) {
            throw new CustomTalkSparkException(ErrorCode.INVALID_FORMAT);
        }

    }
}
