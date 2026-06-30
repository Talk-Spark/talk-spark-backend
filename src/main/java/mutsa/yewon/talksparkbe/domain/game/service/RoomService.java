package mutsa.yewon.talksparkbe.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.domain.card.repository.CardRepository;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomCreateRequest;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomJoinRequest;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomParticipateRepository;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse.RoomDetailsResponse;
import mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse.RoomListResponse;
import mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse.RoomParticipantResponse;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRedisRepository;
import mutsa.yewon.talksparkbe.domain.game.service.util.RoomParticipantInfo;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mutsa.yewon.talksparkbe.global.util.SecurityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class RoomService {

    private final SparkUserRepository sparkUserRepository;

    private final RoomRepository roomRepository;

    private final RoomParticipateRepository roomParticipateRepository;

    private final RedissonClient redissonClient;

    private final StringRedisTemplate redisTemplate;

    private final RoomRedisRepository roomRedisRepository;

    private final JWTUtil jwtUtil;

    private static final String ROOM_COUNT_KEY = "room:participateCount";
    private final CardRepository cardRepository;

    @Transactional
    public Room createRoom(RoomCreateRequest roomCreateRequest, Long sparkUserId) {
        if (roomRepository.findByRoomName(roomCreateRequest.getRoomName()).isPresent())
            throw new CustomTalkSparkException(ErrorCode.ROOM_NAME_DUPLICATE);
        try {
            Room room = Room.builder()
                    .roomName(roomCreateRequest.getRoomName())
                    .difficulty(roomCreateRequest.getDifficulty())
                    .maxPeople(roomCreateRequest.getMaxPeople())
                    .hostId(sparkUserId)
                    .build();
            return roomRepository.save(room);

        } catch (DataIntegrityViolationException e) {
            // 3. 동시성 이슈로 뚫려서 들어온 요청을 여기서 최종 방어
            throw new CustomTalkSparkException(ErrorCode.ROOM_NAME_DUPLICATE);
        }
    }

    @Transactional
    public void joinRoom(RoomJoinRequest roomJoinRequest) {
        Room room = roomRepository.findById(roomJoinRequest.getRoomId()).orElseThrow(() -> new RuntimeException("방 못찾음"));

        String jwt = roomJoinRequest.getAccessToken().replace("Bearer ", "");
        Map<String, Object> claims = jwtUtil.validateToken(jwt);
        String kakaoId = (String) claims.get("kakaoId");
        SparkUser sparkUser = sparkUserRepository.findByKakaoId(kakaoId).orElseThrow(() -> new RuntimeException("유저 못찾음"));
        // 방에 입장할 때 락을 획득
        RLock lock = redissonClient.getLock("roomLock:" + roomJoinRequest.getRoomId());
        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) { // 최대 대기 시간 5초, 락 보유 시간 10초로 설정
                if (!canJoin(room, sparkUser)) throw new CustomTalkSparkException(ErrorCode.ROOM_FULL);
                else {
                    boolean isHost = room.getHostId().equals(sparkUser.getId());
                    addParticipateToRoom(room, sparkUser, isHost);
                }
            } else throw new CustomTalkSparkException(ErrorCode.LOCK_TIMEOUT); // 락을 획득하지 못한 경우 (대기 시간 초과)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomTalkSparkException(ErrorCode.ROOM_JOIN_INTERRUPTED);
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    public List<RoomParticipantResponse> getParticipantList(Long roomId) {
        List<RoomParticipantResponse> response = new ArrayList<>();

        for (RoomParticipantInfo info : roomRedisRepository.getParticipants(roomId)) {
            SparkUser sparkUser = sparkUserRepository.findById(info.sparkUserId())
                    .orElseThrow(() -> new RuntimeException("유저 못찾음"));
            response.add(RoomParticipantResponse.from(sparkUser, sparkUser.getCards().get(0), info.owner()));
        }

        return response;
    }

    public Page<RoomListResponse> searchRooms(String searchName, Pageable pageable) {
        Page<Room> rooms = roomRepository.findByRoomNameStartingWith(searchName, pageable);

        return rooms.map(RoomListResponse::from);
    }

    @Transactional
    public void leaveRoom(RoomJoinRequest roomJoinRequest) {
        Room room = roomRepository.findById(roomJoinRequest.getRoomId()).orElseThrow(() -> new RuntimeException("방 못찾음"));

        String jwt = roomJoinRequest.getAccessToken().replace("Bearer ", "");
        Map<String, Object> claims = jwtUtil.validateToken(jwt);
        String kakaoId = (String) claims.get("kakaoId");
        SparkUser sparkUser = sparkUserRepository.findByKakaoId(kakaoId).orElseThrow(() -> new RuntimeException("유저 못찾음"));

        // 방에서 퇴장할 때 락을 획득
        RLock lock = redissonClient.getLock("roomLock:" + roomJoinRequest.getRoomId());

        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                if (!canJoin(room,sparkUser)) throw new CustomTalkSparkException(ErrorCode.ROOM_FULL);
                else {
                    removeParticipateToRoom(room, sparkUser);
                }
            } else throw new CustomTalkSparkException(ErrorCode.LOCK_TIMEOUT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomTalkSparkException(ErrorCode.ROOM_JOIN_INTERRUPTED);
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    public boolean checkHost(Long roomId, SparkUser sparkUser) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new CustomTalkSparkException(ErrorCode.ROOM_NOT_FOUND));
        SparkUser ownerParticipate = sparkUserRepository.findById(room.getHostId()).orElseThrow(() -> new CustomTalkSparkException(ErrorCode.USER_NOT_EXIST));
        return (sparkUser.equals(ownerParticipate));
    }

    public List<RoomListResponse> listAllRooms() {
        List<Room> rooms = roomRepository.findAllWithParticipates();
        List<RoomListResponse> roomListResponses = new ArrayList<>();

        for (Room r : rooms) {
            RoomListResponse response = RoomListResponse.from(r);
            response.setHostName(r.getRoomParticipates().get(0).getSparkUser().getName());
            response.setCurrentPeople(getParticipateCount(r.getRoomId()));

            roomListResponses.add(response);
        }

        return roomListResponses;
    }

    private boolean canJoin(Room room, SparkUser sparkUser) {
        if (roomRedisRepository.hasParticipant(room.getRoomId(), sparkUser.getId())) {
            redisTemplate.opsForHash().increment(ROOM_COUNT_KEY, room.getRoomId().toString(), -1);
        }
        int currentCount = getParticipateCount(room.getRoomId());
        return currentCount < room.getMaxPeople();
    }

    private void addParticipateToRoom(Room room, SparkUser sparkUser, boolean isHost) {
        if (roomRedisRepository.hasParticipant(room.getRoomId(), sparkUser.getId())) return;

        roomRedisRepository.addParticipant(room.getRoomId(), sparkUser.getId(), isHost);
        redisTemplate.opsForHash().increment(ROOM_COUNT_KEY, room.getRoomId().toString(), 1);
    }

    private void removeParticipateToRoom(Room room, SparkUser sparkUser) {
        roomRedisRepository.removeParticipant(room.getRoomId(), sparkUser.getId());
        redisTemplate.opsForHash().increment(ROOM_COUNT_KEY, room.getRoomId().toString(), -1);
    }
    public int getParticipateCount(Long roomId) {
        String count = (String) redisTemplate.opsForHash().get(ROOM_COUNT_KEY, roomId.toString());
        return count != null ? Integer.parseInt(count) : 0;
    }

    @Transactional
    public void changeStarted(Long roomId, String accessToken) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.ROOM_NOT_FOUND));

        String token = accessToken.replace("Bearer ", "");
        Map<String, Object> claims = jwtUtil.validateToken(token);
        String kakaoId = (String) claims.get("kakaoId");
        SparkUser requestUser = sparkUserRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.USER_NOT_EXIST));

        if (!room.getHostId().equals(requestUser.getId())) {
            throw new CustomTalkSparkException(ErrorCode.NOT_HOST);
        }

        int participateCount = getParticipateCount(roomId);
        if (participateCount < 2) {
            throw new CustomTalkSparkException(ErrorCode.NOT_ENOUGH_PLAYERS);
        }

        room.start();
    }

    public void changeFinished(Long roomId) {
        roomRepository.findById(roomId).orElseThrow().finish();
    }

    public String getRoomName(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow().getRoomName();
    }

    public boolean getIsDuplicateRoomName(String roomName) {
        return roomRepository.findByRoomName(roomName).isPresent();
    }

    public RoomDetailsResponse getRoomDetails(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        return RoomDetailsResponse.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .difficulty(room.getDifficulty())
                .maxPeople(room.getMaxPeople())
                .build();
    }
}
