package club.tempvs.user.token;

import club.tempvs.user.dto.TempvsPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Collection;

public class AuthToken extends PreAuthenticatedAuthenticationToken {

    private TempvsPrincipal principal;

    public AuthToken(TempvsPrincipal principal, Collection<SimpleGrantedAuthority> authorities) {
        super(principal.getEmail(), null, authorities);
        this.principal = principal;
    }
}
