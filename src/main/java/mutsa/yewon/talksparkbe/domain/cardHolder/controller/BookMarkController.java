package mutsa.yewon.talksparkbe.domain.cardHolder.controller;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.BookMarkCreateDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.service.BookMarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookMarkController {

    private final BookMarkService bookMarkService;

    @PutMapping("/api/bookmarks")
    public ResponseEntity<?> updateBookMarks(@RequestBody BookMarkCreateDTO bookMarkCreateDTO) {

        Long bookMark = bookMarkService.createBookMark(bookMarkCreateDTO);

        return ResponseEntity.ok(bookMark);
    }
}
