package mutsa.yewon.talksparkbe.domain.guestBook.repository;

import jakarta.validation.constraints.NotNull;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBook;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBookRoomSparkUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestBookRoomSparkUserRepository extends JpaRepository<GuestBookRoomSparkUser, Long> {

    @Query("select r from GuestBookRoomSparkUser r where r.guestBookRoom.room.roomId = :roomId and r.sparkUser.id = :sparkUserId")
    Optional<GuestBookRoomSparkUser> findByGuestBookRoomIdAndSparkUserId(@NotNull(message = "roomId는 반드시 필요합니다.") Long roomId,
                                                                         @NotNull(message = "sparkUserId는 반드시 필요합니다.") Long sparkUserId);
}
