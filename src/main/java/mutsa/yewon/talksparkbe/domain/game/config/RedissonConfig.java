package mutsa.yewon.talksparkbe.domain.game.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 단일 노드 Redis 서버 주소 설정
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379") // Redis 서버 주소와 포트
                .setConnectionPoolSize(10) // 연결 풀 크기 설정
                .setConnectionMinimumIdleSize(5); // 최소 유휴 연결 수 설정

        return Redisson.create(config);
    }

}

