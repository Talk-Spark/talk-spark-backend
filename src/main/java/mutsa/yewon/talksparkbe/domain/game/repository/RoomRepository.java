package mutsa.yewon.talksparkbe.domain.game.repository;

import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    // 검색어로 시작하는(검색어%) 방 찾기
    Page<Room> findByRoomNameStartingWith(String searchName, Pageable pageable);

    Optional<Room> findByRoomName(String roomName);

    List<Room> findByIsFinishedTrue();

    @Modifying
    @Query("update Room r set r.isFinished = true where r.roomId = :roomId and r.isFinished=false")
    int updateRoomFinished(@Param("roomId") Long roomId);
}
