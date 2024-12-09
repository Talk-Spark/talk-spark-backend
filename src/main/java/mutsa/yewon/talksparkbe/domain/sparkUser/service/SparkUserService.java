package mutsa.yewon.talksparkbe.domain.sparkUser.service;

import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SparkUserService {


    SparkUserDTO getKakaoUser(String accessToken);
    SparkUserDTO generateTestUser(String name);

//    default SparkUserDTO entityToDTO(SparkUser sparkUser) {
//        return new SparkUserDTO(sparkUser.getKakaoId(), sparkUser.getName(), sparkUser.getPassword(),
//                sparkUser.getRoles().stream().map(role -> role.name()).toList());
//    }
}
