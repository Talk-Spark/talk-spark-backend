package mutsa.yewon.talksparkbe.domain.sparkUser.service;

import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class KakaoUserService {

    public Map<String,String> getKakaoUserNickname(String accessToken) {
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

        WebClient webClient = WebClient.builder().build();

        LinkedHashMap response = webClient.get()
                .uri(kakaoGetUserURL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new CustomTalkSparkException(ErrorCode.INVALID_PARAMETER)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new CustomTalkSparkException(ErrorCode.INTERNAL_SERVER_ERROR)))
                .bodyToMono(LinkedHashMap.class)
                .block();


        String kakaoId = String.valueOf(response.get("id"));

        LinkedHashMap<String, String> properties = (LinkedHashMap<String, String>) response.get("properties");


        String name = properties.get("nickname");


        return Map.of("kakaoId", kakaoId, "name", name);
    }
}
