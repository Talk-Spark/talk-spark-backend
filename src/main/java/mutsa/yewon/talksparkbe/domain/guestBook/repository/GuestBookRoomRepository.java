package mutsa.yewon.talksparkbe.domain.guestBook.repository;

import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBook;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBookRoom;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestBookRoomRepository extends JpaRepository<GuestBookRoom, Long> {
    @Query("select r from GuestBookRoom r join r.room rp where rp.roomId = :roomId")
    GuestBookRoom findByRoomId(Long roomId);

    @Query("select r from GuestBookRoom r join r.guestBookRoomSparkUsers rp where rp.sparkUser.id = :sparkUserId")
    List<GuestBookRoom> findRoomsBySparkUser(Long sparkUserId);
}
