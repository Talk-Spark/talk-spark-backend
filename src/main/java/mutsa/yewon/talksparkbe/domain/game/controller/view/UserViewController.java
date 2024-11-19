//package mutsa.yewon.talksparkbe.domain.game.controller.view;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
//import mutsa.yewon.talksparkbe.domain.sparkUser.service.SparkUserService;
//import mutsa.yewon.talksparkbe.global.util.JWTUtil;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Map;
//
//// TODO: 타임리프 뷰에서 로그인한 채로 테스트하기 위한 임시 컨트롤러입니다. 추후 삭제해야 합니다.
//@Controller
//@RequiredArgsConstructor
//public class UserViewController {
//
//    @Value("${kakao.client.id}")
//    private String clientId;
//
//    @Value("${kakao.redirect.uri}")
//    private String redirectUri;
//
//    private final SparkUserService sparkUserService;
//
//    private final JWTUtil jwtUtil;
//
//    @GetMapping("/views/login")
//    public String login(Model model) {
//        String kakaoLoginUrl = String.format("https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
//                clientId, redirectUri);
//        model.addAttribute("kakaoLoginUrl", kakaoLoginUrl);
//        return "login";
//    }
//
//    @GetMapping("/oauth/kakao/callback")
//    public String kakaoCallback(@RequestParam String code, Model model) {
//        // 카카오로부터 받은 인가 코드로 토큰 요청
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", clientId);
//        params.add("redirect_uri", redirectUri);
//        params.add("code", code);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//
//        ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
//                "https://kauth.kakao.com/oauth/token",
//                request,
//                KakaoTokenResponse.class
//        );
//
//        KakaoTokenResponse tokenResponse = response.getBody();
//        String kakaoAccessToken = tokenResponse.getAccess_token();
//
//        // SparkUser 생성 또는 조회
//        SparkUserDTO sparkUser = sparkUserService.getKakaoUser(kakaoAccessToken);
//
//        // JWT 토큰 생성
//        Map<String, Object> claims = sparkUser.getClaims();
//        String jwtAccessToken = jwtUtil.generateToken(claims, 10);
//        String jwtRefreshToken = jwtUtil.generateToken(claims, 60 * 24);
//
//        // 토큰 정보를 모델에 추가
//        model.addAttribute("jwtAccessToken", jwtAccessToken);
//        model.addAttribute("jwtRefreshToken", jwtRefreshToken);
//
//        return "login";
//    }
//
//}
//
//@Getter
//@NoArgsConstructor
//class KakaoTokenResponse {
//    private String access_token;
//    private String token_type;
//    private String refresh_token;
//    private Integer expires_in;
//    private String scope;
//}