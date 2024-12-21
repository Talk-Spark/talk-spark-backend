package mutsa.yewon.talksparkbe.domain.sparkUser.dto;

import lombok.Getter;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;

@Getter
public class SparkUserDTO extends User {

    private Long sparkUserId;

    private String kakaoId;

    private String name;

    private String password;

    private List<String> roleNames = new ArrayList<>();

    public SparkUserDTO(Long sparkUserId, String kakaoId, String name, String password, List<String> roleNames) {
        super(kakaoId, password,  roleNames.stream()
                .map(str -> new SimpleGrantedAuthority("ROLE_" + str)).toList());

        this.sparkUserId = sparkUserId;
        this.kakaoId = kakaoId;
        this.name = name;
        this.password = password;
        this.roleNames = roleNames;
    }

    public static SparkUserDTO from(SparkUser sparkUser) {
        return new SparkUserDTO(sparkUser.getId(), sparkUser.getKakaoId(), sparkUser.getName(), sparkUser.getPassword(),
                sparkUser.getRoles().stream().map(role -> role.name()).toList());
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();

        claims.put("sparkUserId", sparkUserId);
        claims.put("kakaoId", kakaoId);
        claims.put("name", name);
        claims.put("password", password);
        claims.put("roleNames", roleNames);
        return claims;
    }



}
