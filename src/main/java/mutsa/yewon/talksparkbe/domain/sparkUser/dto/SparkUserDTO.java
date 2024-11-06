package mutsa.yewon.talksparkbe.domain.sparkUser.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;

public class SparkUserDTO extends User {

    private String kakaoId;

    private String name;

    private String password;

    private List<String> roleNames = new ArrayList<>();

    public SparkUserDTO(String kakaoId, String name, String password, List<String> roleNames) {
        super(kakaoId, password,  roleNames.stream()
                .map(str -> new SimpleGrantedAuthority("ROLE_" + str)).toList());

        this.kakaoId = kakaoId;
        this.name = name;
        this.password = password;
        this.roleNames = roleNames;
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();

        claims.put("kakaoId", kakaoId);
        claims.put("name", name);
        claims.put("password", password);
        claims.put("roleNames", roleNames);
        return claims;
    }



}
