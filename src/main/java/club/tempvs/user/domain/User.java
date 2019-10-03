package club.tempvs.user.domain;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.FetchType.EAGER;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(unique=true)
    private String email;
    private String password;
    private Boolean accountNonExpired = Boolean.TRUE;
    private Boolean accountNonLocked = Boolean.TRUE;
    private Boolean credentialsNonExpired = Boolean.TRUE;
    private Boolean enabled = Boolean.TRUE;
    private Long currentProfileId;
    private String timeZone;
    @ElementCollection(fetch = EAGER)
    private Set<String> roles = new HashSet<>();

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
