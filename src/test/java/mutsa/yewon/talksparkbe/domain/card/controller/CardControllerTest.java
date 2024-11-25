package mutsa.yewon.talksparkbe.domain.card.controller;

import mutsa.yewon.talksparkbe.domain.card.dto.CardCreateDTO;
import mutsa.yewon.talksparkbe.domain.card.dto.CardResponseDTO;
import mutsa.yewon.talksparkbe.domain.card.service.CardService;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUserRole;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import mutsa.yewon.talksparkbe.domain.sparkUser.service.SparkUserService;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CardControllerTest {

    @Autowired
    private CardService cardService;

    @Autowired
    private SparkUserRepository sparkUserRepository;

    @BeforeEach
    void setUp() {
        SparkUser sparkUser = SparkUser.builder()
                .kakaoId("333335555")
                .password("52154")
                .name("박승범")
                .roles(List.of(SparkUserRole.USER))
                .build();

        sparkUserRepository.save(sparkUser);

        cardService.createCard(new CardCreateDTO(Long.parseLong("1"), "박승범", 24, "컴퓨터공학과", "ISTJ", "코딩", "너구리", "코딩하는 너구리", "TalkSpark"));
        cardService.createCard(new CardCreateDTO(Long.parseLong("1"), "박승범", 24, "컴퓨터공학과", "ISTJ", "코딩", "너구리", "코딩하는 너구리", "TalkSparkIsFun"));
        cardService.createCard(new CardCreateDTO(Long.parseLong("1"), "박승범", 24, "컴퓨터공학과", "ISTJ", "코딩", "너구리", "코딩은 나의 삶, 나의 안식", "TalkSpark"));

    }

    @Test
    @DisplayName("명함 생성 성공을 검증한다.")
    public void createCard() {
        CardCreateDTO cardCreateDTO = new CardCreateDTO(Long.parseLong("1"), "박승범", 24, "컴퓨터공학과", "ISTJ", "코딩", "너구리", "코딩하는 너구리", "TalkSpark");
        Long cardId = cardService.createCard(cardCreateDTO);

        assertNotNull(cardId);
        assertEquals(Long.parseLong("1"), cardId);
    }

    @Test
    @DisplayName("사용자의 모든 명함을 조회합니다.")
    public void getCards() {
        List<CardResponseDTO> cards = cardService.getCards(1L);

        assertNotNull(cards);
        assertEquals(3, cards.size());
        assertEquals("333335555", cards.get(0).getKakaoId());
        assertEquals("333335555", cards.get(1).getKakaoId());
        assertEquals("333335555", cards.get(2).getKakaoId());
    }

    @Test
    @DisplayName("사용자의 명함을 수정합니다.")
    @WithMockUser(username = "333335555", roles = {"USER"})
    public void modifyCard() {
        CardCreateDTO cardCreateDTO =
                new CardCreateDTO(Long.parseLong("1"), "박승범", 25, "컴퓨터공학과", "ISTJ", "운동", "너구리", "코딩하는 너구리", "");

        Map<String, Long> modifiedCard = cardService.modifyCard(1L, cardCreateDTO);

        assertNotNull(modifiedCard);
        assertEquals(modifiedCard.get("UPDATED CARD"), 1L);

    }

    @Test
    @DisplayName("사용자 명함을 삭제합니다.")
    @WithMockUser(username = "333335555", roles = {"USER"})
    public void deleteCard() {
        Map<String, Long> deletedCard = cardService.deleteCard(1L);

        assertNotNull(deletedCard);
        assertEquals(deletedCard.get("DELETE COMPLETED"), 1L);
    }

    @Test
    @DisplayName("본인의 명함이 아닌 명함에 대한 수정/삭제 요청은 거부됩니다.")
    @WithMockUser(username = "333335556", roles = {"USER"})
    public void ownerCheck(){
        CardCreateDTO cardCreateDTO =
                new CardCreateDTO(Long.parseLong("1"), "박승범", 25, "컴퓨터공학과", "ISTJ", "운동", "너구리", "코딩하는 너구리", "");

        assertThrows(CustomTalkSparkException.class, () -> {
            cardService.modifyCard(1L, cardCreateDTO);
        });

        assertThrows(CustomTalkSparkException.class, () -> {
            cardService.deleteCard(1L);
        });

    }
}