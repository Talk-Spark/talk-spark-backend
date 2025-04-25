package mutsa.yewon.talksparkbe.domain.sparkUser.service;

import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.service.response.SparkUserResponse;
import mutsa.yewon.talksparkbe.domain.sparkUser.service.response.TokenResponse;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface SparkUserService {


    SparkUserResponse loginOrRegister(String kakaoAccessToken);
    SparkUserDTO generateTestUser(String name);
    Long deleteAccount(String accessToken);
    TokenResponse reissueToken(String refreshToken);

//    default SparkUserDTO entityToDTO(SparkUser sparkUser) {
//        return new SparkUserDTO(sparkUser.getKakaoId(), sparkUser.getName(), sparkUser.getPassword(),
//                sparkUser.getRoles().stream().map(role -> role.name()).toList());
//    }
}
