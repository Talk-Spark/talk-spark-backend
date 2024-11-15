package mutsa.yewon.talksparkbe.domain.sparkUser.entity;

import jakarta.persistence.*;
import lombok.*;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBook;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBookRoom;
import mutsa.yewon.talksparkbe.domain.guestBook.entity.GuestBookRoomSparkUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy = "sparkUser")
    private List<GuestBookRoomSparkUser> guestBookRoomSparkUsers = new ArrayList<>();
}
