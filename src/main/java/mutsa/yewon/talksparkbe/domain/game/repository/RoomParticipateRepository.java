package mutsa.yewon.talksparkbe.domain.game.repository;

import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomParticipateRepository extends JpaRepository<RoomParticipate, Long> {

    @Query("select rp " +
            "from RoomParticipate rp " +
            "join fetch rp.sparkUser su " +
            "where rp.room.roomId = :roomId")
    List<RoomParticipate> findByRoomIdWithSparkUser(Long roomId);

    @Query("select rp " +
            "from RoomParticipate rp " +
            "join fetch rp.sparkUser su " +
            "where rp.room.roomId = :roomId and rp.isOwner = true ")
    Optional<RoomParticipate> findByRoomIdWithOwner(Long roomId);

    Optional<RoomParticipate> findByRoomAndSparkUser(Room room, SparkUser sparkUser);

}
