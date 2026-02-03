package mutsa.yewon.talksparkbe.domain.game.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.service.util.GameStateManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveGameState(Long roomId, GameStateManager gameStateManager) {
        String key = "game:" + roomId;
        redisTemplate.opsForValue().set(key,gameStateManager);
    }

    public GameStateManager getGameState(Long roomId) {
        String key = "game:" + roomId;
        Object rawData = redisTemplate.opsForValue().get(key);

        if (rawData == null) return null;

        return objectMapper.convertValue(rawData, GameStateManager.class);
    }

    public void deleteGameState(Long roomId) {
        String key = "game:" + roomId;
        redisTemplate.delete(key);
    }
}
