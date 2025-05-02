package mutsa.yewon.talksparkbe.domain.sparkUser.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class SparkUserResponse {

    private Map<String, Object> claims;

    private String accessToken;

    private String refreshToken;

}
