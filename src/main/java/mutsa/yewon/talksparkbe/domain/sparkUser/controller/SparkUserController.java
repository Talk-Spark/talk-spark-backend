package mutsa.yewon.talksparkbe.domain.sparkUser.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.domain.sparkUser.SparkUserControllerDocs;
import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserResponseDto;
import mutsa.yewon.talksparkbe.domain.sparkUser.service.RefreshTokenService;
import mutsa.yewon.talksparkbe.domain.sparkUser.service.SparkUserService;
import mutsa.yewon.talksparkbe.global.dto.ResponseDTO;
import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import mutsa.yewon.talksparkbe.global.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class SparkUserController implements SparkUserControllerDocs {

    private final SparkUserService sparkUserService;

    private final JWTUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;

    private SecurityUtil securityUtil;


    @PostMapping("/api/member/kakao") // 반환 객체 수정
    public ResponseEntity<SparkUserResponseDto> login(@RequestParam("accessToken") String accessToken,
                                                     HttpServletResponse response) {
        log.info(accessToken);

        SparkUserDTO kakaoUser = sparkUserService.getKakaoUser(accessToken);

        Map<String, Object> claims = kakaoUser.getClaims();

        String JWTAccessToken = jwtUtil.generateToken(claims, 60 * 12);
        String JWTRefreshToken = jwtUtil.generateToken(claims, 60 * 24);

        refreshTokenService.saveRefreshToken(JWTRefreshToken, kakaoUser.getSparkUserId());

        response.addCookie(createRefresTokenCookie(JWTAccessToken));

        return ResponseEntity.ok(SparkUserResponseDto.from(kakaoUser, JWTAccessToken));
    }

    @GetMapping("/api/member/refresh")
    public Map<String, String> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                       HttpServletResponse response) {


        Map<String, Object> claims = jwtUtil.validateToken(refreshToken);

        Map<String, String> newTokenPair =
                refreshTokenService.getNewRefreshToken(claims, refreshToken);
//
//        String accessToken = jwtUtil.generateToken(claims, 60);
//
//        Map<String, String> newJwtToken =
//                refreshTokenService.getNewRefreshToken((Long) claims.get("sparkUserId"), refreshToken);
//
//        return newJwtToken;

        Cookie refresTokenCookie = createRefresTokenCookie(newTokenPair.get("refreshToken"));

        response.addCookie(refresTokenCookie);

        return Map.of("accessToken", newTokenPair.get("accessToken"));

    }

    @DeleteMapping("/api/member/leave")
    public ResponseEntity<?> accountDelete(String accessToken) {
        // jwtfilter를 거치지 않기 때문에 등록 x

//        Map<String, Object> claims = jwtUtil.validateToken(accessToken);

        Long sparkUserId = securityUtil.getLoggedInUserId();

//        Number sparkUserIdNumber = (Number) claims.get("sparkUserId");
//        Long sparkUserId = (sparkUserIdNumber != null) ? sparkUserIdNumber.longValue() : null;

        Long deletedUserId = sparkUserService.deleteAccount(sparkUserId);

        return ResponseEntity.ok().body(ResponseDTO.ok("회원정보가 삭제되었습니다.", deletedUserId));
    }

    private Cookie createRefresTokenCookie(String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("JWTRefreshToken", refreshToken);

        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 24);

        return refreshTokenCookie;
    }
}
