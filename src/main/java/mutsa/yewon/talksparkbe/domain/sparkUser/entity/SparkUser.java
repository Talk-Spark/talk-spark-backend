package mutsa.yewon.talksparkbe.domain.sparkUser.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@ToString(exclude = "roles")
public class SparkUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spark_user_id")
    private Long id;

    private String name;

    private String kakaoId;

    private String password;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    private List<SparkUserRole> roles = new ArrayList<>();

    public void addMemberRole(SparkUserRole memberRole) {
        roles.add(memberRole);
    }

    public void clearMemberRoleList() {
        roles.clear();
    }
}
