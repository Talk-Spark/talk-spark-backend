package mutsa.yewon.talksparkbe.domain.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameStateService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void updatScore(String gameId, Long userId, int score){
        String key = "game:" + gameId + ":scores";
        redisTemplate.opsForZSet().incrementScore(key, userId.toString(), score);
    }


}
