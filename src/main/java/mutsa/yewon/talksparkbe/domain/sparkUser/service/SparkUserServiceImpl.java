package mutsa.yewon.talksparkbe.domain.sparkUser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUserRole;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
@Service
public class SparkUserServiceImpl implements SparkUserService {

    private final SparkUserRepository sparkUserRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public SparkUserDTO getKakaoUser(String accessToken) {
        Map<String, String> kakaoData = getKakaoUserNickname(accessToken);

        String kakaoId = kakaoData.get("kakaoId");
        String name = kakaoData.get("name");

        Optional<SparkUser> sparkUser = sparkUserRepository.findByKakaoId(kakaoId);

        if(sparkUser.isPresent()) {
            SparkUserDTO sparkUserDTO = SparkUserDTO.from(sparkUser.get());
            return sparkUserDTO;
        }

        SparkUser createdUser = makeSparkUser(kakaoId,name);

        sparkUserRepository.save(createdUser);

        SparkUserDTO sparkUserDTO = SparkUserDTO.from(createdUser);

        return sparkUserDTO;
    }

    private Map<String,String> getKakaoUserNickname(String accessToken) {
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

        WebClient webClient = WebClient.builder().build();

        LinkedHashMap response = webClient.get()
                .uri(kakaoGetUserURL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .bodyToMono(LinkedHashMap.class)
                .block();


        String kakaoId = String.valueOf(response.get("id"));

        LinkedHashMap<String, String> properties = (LinkedHashMap<String, String>) response.get("properties");


        String name = properties.get("nickname");


        return Map.of("kakaoId", kakaoId, "name", name);
    }

    private SparkUser makeSparkUser(String kakaoId, String name) {
        String tempPassword = makeTempPassword();

        SparkUser sparkUser = SparkUser.builder()
                .kakaoId(kakaoId)
                .name(name)
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
}
