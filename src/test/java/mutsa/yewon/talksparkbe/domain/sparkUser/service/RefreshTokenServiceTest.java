package mutsa.yewon.talksparkbe.domain.sparkUser.service;

import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class RefreshTokenServiceTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JWTUtil jwtUtil;

    @DisplayName("테스트 코드 작성법")
    @Test
    void 새로운_테스트(){

        //given

        //when

        //then
    }



    @DisplayName("Redis에 Refresh Token을 저장한다.")
    @Test
    void saveRefreshToken(){

        //given
        Map<String, Object> tokenClaim = new HashMap<>();

        Long userId = 1L;

        tokenClaim.put("userName", "박승범");
        tokenClaim.put("sparkUserId", userId);
        tokenClaim.put("favoriteColor", "red");

        String refreshToken = jwtUtil.generateToken(tokenClaim, 60 * 24);


        //when
        refreshTokenService.saveRefreshToken(refreshToken);

        //then
        String key = "refresh:user" + userId;
        String storedToken = redisTemplate.opsForValue().get(key);

        log.info(storedToken);

        // 3. 검증 (저장한 Refresh Token과 Redis에 저장된 값이 같은지 확인)
        assertThat(storedToken).isNotNull();
        assertThat(storedToken).isEqualTo(refreshToken);

    }

    @DisplayName("RefreshToken으로 새롭게 Access Token과 Refresh Token을 발급해주면 기존 RefreshToken은 삭제한다.")
    @Test
    void getNewRefreshToken(){

        //given
        Map<String, Object> tokenClaim = new HashMap<>();

        tokenClaim.put("userName", "박승범");
        tokenClaim.put("userId", 1L);
        tokenClaim.put("favoriteColor", "red");

        String refreshToken = jwtUtil.generateToken(tokenClaim, 60 * 24);

        log.info("beforeRefreshToken = " + refreshToken);

        Long userId = 1L;

        String key = "refresh:user" + userId;

        refreshTokenService.saveRefreshToken(refreshToken);

        //when
        Map<String, String> newJwtToken = refreshTokenService.getNewRefreshToken(refreshToken);

        //then
        assertThat(newJwtToken).isNotNull();
        assertThat(redisTemplate.opsForValue().get(key)).isNotEqualTo(refreshToken);
    }

    @DisplayName("기존 RefreshToken을 삭제한다.")
    @Test
    void deleteRefreshToken(){
        //given
        Map<String, Object> tokenClaim = new HashMap<>();

        tokenClaim.put("userName", "박승범");
        tokenClaim.put("userId", 1L);

        String refreshToken = jwtUtil.generateToken(tokenClaim, 60 * 24);

        log.info(refreshToken);

        Long userId = 1L;

        String key = "refresh:user" + userId;

        refreshTokenService.saveRefreshToken(refreshToken);

        //when
        redisTemplate.delete(key);
        System.out.println("refreshToken = " + redisTemplate.opsForValue().get(key));

        //then
        assertThat(redisTemplate.opsForValue().get(key)).isNotEqualTo(refreshToken);
    }

}