package mutsa.yewon.talksparkbe.domain.game.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.service.util.RoomParticipantInfo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RoomRedisRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "room:participants:";

    public void addParticipant(Long roomId, Long sparkUserId, boolean isOwner) {
        List<RoomParticipantInfo> participants = getParticipants(roomId);
        boolean alreadyExists = participants.stream()
                .anyMatch(p -> p.sparkUserId().equals(sparkUserId));
        if (!alreadyExists) {
            participants.add(new RoomParticipantInfo(sparkUserId, isOwner));
            save(roomId, participants);
        }
    }

    public void removeParticipant(Long roomId, Long sparkUserId) {
        List<RoomParticipantInfo> participants = getParticipants(roomId);
        participants.removeIf(p -> p.sparkUserId().equals(sparkUserId));
        save(roomId, participants);
    }

    public List<RoomParticipantInfo> getParticipants(Long roomId) {
        String json = stringRedisTemplate.opsForValue().get(KEY_PREFIX + roomId);
        if (json == null) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<RoomParticipantInfo>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public boolean hasParticipant(Long roomId, Long sparkUserId) {
        return getParticipants(roomId).stream()
                .anyMatch(p -> p.sparkUserId().equals(sparkUserId));
    }

    public void clearParticipants(Long roomId) {
        stringRedisTemplate.delete(KEY_PREFIX + roomId);
    }

    private void save(Long roomId, List<RoomParticipantInfo> participants) {
        try {
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + roomId, objectMapper.writeValueAsString(participants));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("방 참가자 정보 직렬화 실패", e);
        }
    }
}
