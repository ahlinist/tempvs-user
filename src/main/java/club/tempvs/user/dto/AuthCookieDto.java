package club.tempvs.user.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class AuthCookieDto {

    private Long userId;
    private String email;
    private Long currentProfileId;
    private String timeZone;
    private String lang;
    private Set<String> roles = new HashSet<>();
}
