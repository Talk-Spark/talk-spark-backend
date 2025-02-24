package mutsa.yewon.talksparkbe.domain.sparkUser.service;

import mutsa.yewon.talksparkbe.domain.card.repository.CardRepository;
import mutsa.yewon.talksparkbe.domain.cardHolder.repository.CardHolderRepository;
import mutsa.yewon.talksparkbe.domain.guestBook.repository.GuestBookRoomRepository;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class SparkUserServiceTest {

    @Autowired
    private SparkUserService sparkUserService;

    @Autowired
    private SparkUserRepository sparkUserRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardHolderRepository cardHolderRepository;

    @Autowired
    private GuestBookRoomRepository guestBookRoomRepository;

    @DisplayName("회원 정보 삭제 시, 회원의 명함, 회원이 저장했던 명함들은 삭제되고 회원이 방명록 방에 참여했던 이력은 남아있다.")
    @Test
    void accountDeleteWithCascade(){

        //given


        //when

        //then

    }

}
