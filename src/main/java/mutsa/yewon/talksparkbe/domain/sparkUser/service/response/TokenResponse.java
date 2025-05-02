package mutsa.yewon.talksparkbe.domain.sparkUser.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
public class TokenResponse {

    private String newAccessToken;
    private String newRefreshToken;

    public static TokenResponse from(Map<String, String> tokens) {
        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.newAccessToken = tokens.get("accessToken");
        tokenResponse.newRefreshToken = tokens.get("refreshToken");

        return tokenResponse;
    }
}
