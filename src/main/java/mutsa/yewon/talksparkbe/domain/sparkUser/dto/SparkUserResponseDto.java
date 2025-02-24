package mutsa.yewon.talksparkbe.domain.sparkUser.dto;

import lombok.Builder;
import lombok.Getter;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUserRole;

import java.util.List;

@Getter
public class SparkUserResponseDto {

    private Long sparkUserId;

    private String accessToken;

    private List<String> sparkUserRoles;

    private String sparkUserName;

    private String kakaoId;

    @Builder
    public SparkUserResponseDto(Long sparkUserId, String accessToken,
                                List<String> sparkUserRoles, String sparkUserName, String kakaoId) {
        this.sparkUserId = sparkUserId;
        this.accessToken = accessToken;
        this.sparkUserRoles = sparkUserRoles;
        this.sparkUserName = sparkUserName;
        this.kakaoId = kakaoId;
    }

    public static SparkUserResponseDto from(SparkUserDTO sparkUserDTO, String accessToken) {
        return SparkUserResponseDto.builder()
                .sparkUserId(sparkUserDTO.getSparkUserId())
                .sparkUserRoles(sparkUserDTO.getRoleNames())
                .sparkUserName(sparkUserDTO.getName())
                .kakaoId(sparkUserDTO.getKakaoId())
                .accessToken(accessToken).build();
    }
}
