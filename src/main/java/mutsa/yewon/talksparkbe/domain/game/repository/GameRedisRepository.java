package mutsa.yewon.talksparkbe.domain.game.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.service.util.GameStateManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRedisRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "game:";

    public void saveGameState(Long roomId, GameStateManager gameStateManager) {
        try {
            String json = objectMapper.writeValueAsString(gameStateManager);
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + roomId, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("GameStateManager 직렬화 실패: roomId=" + roomId, e);
        }
    }

    public GameStateManager getGameState(Long roomId) {
        String json = stringRedisTemplate.opsForValue().get(KEY_PREFIX + roomId);
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, GameStateManager.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("GameStateManager 역직렬화 실패: roomId=" + roomId, e);
        }
    }

    public boolean existsGameState(Long roomId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(KEY_PREFIX + roomId));
    }

    public void deleteGameState(Long roomId) {
        stringRedisTemplate.delete(KEY_PREFIX + roomId);
    }
}
