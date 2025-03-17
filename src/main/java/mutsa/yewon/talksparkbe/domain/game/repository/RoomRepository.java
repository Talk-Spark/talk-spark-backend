package mutsa.yewon.talksparkbe.domain.game.repository;

import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("select r " +
            "from Room r " +
            "join fetch r.roomParticipates rp " +
            "where rp.isOwner = true and r.isFinished = false ")
    List<Room> findAllWithParticipates();


    @Query("select r " +
            "from Room r " +
            "where r.isStarted = false and r.isFinished = false ")
    List<Room> findByRoomNameContaining(String searchName);

    Optional<Room> findByRoomName(String roomName);

    List<Room> findByIsFinishedTrue();

    @Modifying
    @Query("update Room r set r.isFinished = true where r.roomId = :roomId and r.isFinished=false")
    int updateRoomFinished(@Param("roomId") Long roomId);
}
