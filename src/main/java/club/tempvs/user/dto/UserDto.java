package club.tempvs.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Null;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {

    @Null
    private Long id;
    @Email
    private String email;
    @JsonProperty(access = WRITE_ONLY)
    private String password;
    @JsonIgnore
    private Boolean accountNonExpired;
    @JsonIgnore
    private Boolean accountNonLocked;
    @JsonIgnore
    private Boolean credentialsNonExpired;
    @JsonIgnore
    private Boolean enabled;
    private Long currentProfileId;
    private String timeZone;
    private Set<String> roles = new HashSet<>();
}
