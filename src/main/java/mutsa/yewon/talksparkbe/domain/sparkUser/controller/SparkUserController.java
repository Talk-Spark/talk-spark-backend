package mutsa.yewon.talksparkbe.domain.sparkUser.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.domain.sparkUser.SparkUserControllerDocs;
import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
import mutsa.yewon.talksparkbe.domain.sparkUser.service.SparkUserService;
import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class SparkUserController implements SparkUserControllerDocs {

    private final SparkUserService sparkUserService;
    private final JWTUtil jwtUtil;

    @PostMapping("/api/member/kakao")
    public Map<String, Object> login(String accessToken) {
        SparkUserDTO kakaoUser = sparkUserService.getKakaoUser(accessToken);

        Map<String, Object> claims = kakaoUser.getClaims();

        String JWTAccessToken = jwtUtil.generateToken(claims, 10);
        String JWTRefreshToken = jwtUtil.generateToken(claims, 60 * 24);

        claims.put("accessToken", JWTAccessToken);
        claims.put("refreshToken", JWTRefreshToken);

        return claims;
    }

    @GetMapping("/api/member/refresh")
    public Map<String, Object> refresh(String refreshToken) {
        Map<String, Object> claims = jwtUtil.validateToken(refreshToken);

        String accessToken = jwtUtil.generateToken(claims, 10);

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }
}
