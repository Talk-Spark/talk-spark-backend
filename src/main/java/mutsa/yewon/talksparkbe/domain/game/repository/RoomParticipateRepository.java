package mutsa.yewon.talksparkbe.domain.game.repository;

import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomParticipateRepository extends JpaRepository<RoomParticipate, Long> {

    @Query("select rp " +
            "from RoomParticipate rp " +
            "join fetch rp.sparkUser su " +
            "where rp.room.roomId = :roomId")
    List<RoomParticipate> findByRoomIdWithSparkUser(Long roomId);

}
