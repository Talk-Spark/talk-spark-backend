package mutsa.yewon.talksparkbe.domain.cardHolder.service;

import mutsa.yewon.talksparkbe.domain.card.dto.CardCreateDTO;
import mutsa.yewon.talksparkbe.domain.card.service.CardService;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.BookMarkCreateDTO;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUserRole;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookMarkServiceImplTest {

    @Autowired
    private BookMarkService bookMarkService;

    @Autowired
    private SparkUserRepository sparkUserRepository;

    @Autowired
    private CardService cardService;

    @BeforeEach
    void setUp() {
        SparkUser sparkUser1 = SparkUser.builder()
                .kakaoId("333335555")
                .password("52154")
                .name("박승범")
                .roles(List.of(SparkUserRole.USER))
                .build();

        SparkUser sparkUser2 = SparkUser.builder()
                .kakaoId("333335556")
                .password("521533")
                .name("김민우")
                .roles(List.of(SparkUserRole.USER))
                .build();

        sparkUserRepository.save(sparkUser1);
        sparkUserRepository.save(sparkUser2);

        cardService.createCard(new CardCreateDTO(Long.parseLong("1"), "박승범", 24, "컴퓨터공학과", "ISTJ", "코딩", "너구리", "코딩하는 너구리", "TalkSpark"));
        cardService.createCard(new CardCreateDTO(Long.parseLong("1"), "박승범", 24, "컴퓨터공학과", "ISTJ", "코딩", "너구리", "코딩하는 너구리", "TalkSparkIsFun"));
        cardService.createCard(new CardCreateDTO(Long.parseLong("1"), "박승범", 24, "컴퓨터공학과", "ISTJ", "코딩", "너구리", "코딩은 나의 삶, 나의 안식", "TalkSpark"));
        cardService.createCard(new CardCreateDTO(Long.parseLong("2"), "김민우", 24, "컴퓨터공학과", "INTP", "코딩", "김민우", "코딩하는 김민우", "TalkSpark!!!"));

    }

    @Test
    @DisplayName("즐겨찾기를 생성합니다.")
    public void createBookMark() {
        BookMarkCreateDTO bookMarkCreateDTO = new BookMarkCreateDTO(4L, 1L);

        Long bookMarkId = bookMarkService.createBookMark(bookMarkCreateDTO);

        assertNotNull(bookMarkId);

        assertEquals(1L, bookMarkId);
    }
}