package mutsa.yewon.talksparkbe.domain.sparkUser.controller;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
import mutsa.yewon.talksparkbe.domain.sparkUser.service.SparkUserService;
import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestUserController {

    private final SparkUserService sparkUserService;
    private final JWTUtil jwtUtil;

    @PostMapping("/api/test-user")
    public Map<String, Object> login(@RequestBody String name) {
        SparkUserDTO testUser = sparkUserService.generateTestUser(name);

        Map<String, Object> claims = testUser.getClaims();

        String JWTAccessToken = jwtUtil.generateToken(claims, 60 * 12);
        String JWTRefreshToken = jwtUtil.generateToken(claims, 60 * 24);

        claims.put("accessToken", JWTAccessToken);
        claims.put("refreshToken", JWTRefreshToken);

        return claims;
    }

    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Authorization")
    @GetMapping("/api/user-id")
    public Long getMyUserId(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        Map<String, Object> claims = jwtUtil.validateToken(jwt);
        System.out.println("claims.get(\"sparkUserId\") = " + claims.get("sparkUserId"));
        return Long.valueOf(claims.get("sparkUserId").toString());
    }

}
