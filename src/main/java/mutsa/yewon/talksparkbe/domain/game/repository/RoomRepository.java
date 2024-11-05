package mutsa.yewon.talksparkbe.domain.game.repository;

import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("select r " +
            "from Room r " +
            "join fetch r.roomParticipates rp " +
            "where rp.isOwner = true and r.isFinished = false ")
    List<Room> findAllWithParticipates();

}
