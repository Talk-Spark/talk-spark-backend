package mutsa.yewon.talksparkbe.domain.guestBook.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.GuestBookContentDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.dto.GuestBookPostRequestDTO;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBook;
import mutsa.yewon.talksparkbe.domain.guestBook.service.GuestBookService;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.global.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/guestBooks")
@RestController
@RequiredArgsConstructor
public class GuestBookController {
    private final GuestBookService guestBookService;

    @PostMapping("/{roomId}")
//@AuthenticationPrincipal SparkUser sparkUser
    public ResponseEntity<ResponseDTO<?>> PostGuestBook(@RequestHeader Long sparkUserId,
                                                        @PathVariable("roomId") Long roomId,
                                                        @Valid @RequestBody GuestBookContentDTO contentDTO) {
        try {
            GuestBookPostRequestDTO guestBookPostRequestDTO = new GuestBookPostRequestDTO(roomId, sparkUserId, contentDTO);
            GuestBook guestBook = guestBookService.createGuestBook(guestBookPostRequestDTO);
            ResponseDTO<?> responseDTO = ResponseDTO.ok("작성되었습니다.");
            return ResponseEntity.status(204).body(responseDTO);
        } catch (IllegalArgumentException e) {
            ResponseDTO<?> responseDTO = ResponseDTO.notFound(e.getMessage());
            return ResponseEntity.status(404).body(responseDTO);
        }

    }
}
