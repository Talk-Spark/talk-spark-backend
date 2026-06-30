package mutsa.yewon.talksparkbe.domain.sparkUser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUserRole;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import mutsa.yewon.talksparkbe.domain.sparkUser.service.response.SparkUserResponse;
import mutsa.yewon.talksparkbe.domain.sparkUser.service.response.TokenResponse;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import mutsa.yewon.talksparkbe.global.util.SecurityUtil;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Log4j2
@Service
public class SparkUserServiceImpl implements SparkUserService {

    private final SparkUserRepository sparkUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;

    private final KakaoUserService kakaoUserService;

    @Override
    @Transactional
    public SparkUserResponse loginOrRegister(String kakaoAccessToken) {

        Map<String, String> kakaoData = kakaoUserService.getKakaoUserNickname(kakaoAccessToken);

        String kakaoId = kakaoData.get("kakaoId");
        String name = kakaoData.get("name");

        Optional<SparkUser> sparkUser = sparkUserRepository.findByKakaoId(kakaoId);

        Map<String, Object> claims = null;

        if(sparkUser.isPresent()) {
            claims = SparkUserDTO.from(sparkUser.get()).getClaims();
        }else{
            SparkUser newSparkUser = makeSparkUser(kakaoId, name);
            sparkUserRepository.save(newSparkUser);

            claims = SparkUserDTO.from(newSparkUser).getClaims();
        }

        String accessToken = jwtUtil.generateToken(claims, 60);
        String refreshToken = jwtUtil.generateToken(claims, 60 * 60 * 7);

        refreshTokenService.saveRefreshToken(refreshToken);

        return new SparkUserResponse(claims, accessToken, refreshToken);

    }

    public TokenResponse reissueToken(String refreshToken) {
        return TokenResponse.from(refreshTokenService.getNewRefreshToken(refreshToken));
    }


    private SparkUser makeSparkUser(String kakaoId, String name) {
        String tempPassword = makeTempPassword();

        SparkUser sparkUser = SparkUser.builder()
                .kakaoId(kakaoId)
                .name(name)
                .deleted(false)
                .password(passwordEncoder.encode(tempPassword)).build();

        sparkUser.addMemberRole(SparkUserRole.USER);

        return sparkUser;
    }

    private String makeTempPassword() {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < 10; i++) {
            buffer.append((char)((int)(Math.random() * 55) + 65));
        }

        return buffer.toString();
    }

    @Override
    @Transactional
    public SparkUserDTO generateTestUser(String name) {
        Optional<SparkUser> sparkUser = sparkUserRepository.findByName(name);
        if(sparkUser.isPresent()) {
            return SparkUserDTO.from(sparkUser.get());
        }

        Random random = new Random();
        int randomNumber = random.nextInt(901) + 100; // 901은 (1000 - 100 + 1)
        SparkUser createdUser = makeSparkUser(String.valueOf(randomNumber), name);
        sparkUserRepository.save(createdUser);
        return SparkUserDTO.from(createdUser);
    }

    @Override
    @Transactional
    public Long deleteAccount(String accessToken) {

        Map<String, Object> claims = jwtUtil.validateToken(accessToken);

        Number sparkUserIdNumber = (Number) claims.get("sparkUserId");
        Long sparkUserId = (sparkUserIdNumber != null) ? sparkUserIdNumber.longValue() : null;

        SparkUser sparkUser = sparkUserRepository.findById(sparkUserId)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.USER_NOT_EXIST));

        sparkUser.deleteUser();

        return sparkUser.getId();

//        sparkUserRepository.findById(sparkUserId)
//                        .orElseThrow(()-> new CustomTalkSparkException(ErrorCode.USER_NOT_EXIST));
//
//        sparkUserRepository.deleteById(sparkUserId);
//
//        return sparkUserId;
    }

}
