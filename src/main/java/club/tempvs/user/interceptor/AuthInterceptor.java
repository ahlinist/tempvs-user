package club.tempvs.user.interceptor;

import static java.util.stream.Collectors.*;

import club.tempvs.user.dto.TempvsPrincipal;
import club.tempvs.user.token.AuthToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String USER_INFO_HEADER = "User-Info";

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userInfoHeaderValue = request.getHeader(USER_INFO_HEADER);

        if (userInfoHeaderValue != null) {
            response.setHeader(USER_INFO_HEADER, userInfoHeaderValue);

            TempvsPrincipal principal = objectMapper.readValue(userInfoHeaderValue, TempvsPrincipal.class);
            Set<String> roles = principal.getRoles();
            Set<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(toSet());
            AuthToken authToken = new AuthToken(principal, authorities);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authToken);

            String lang = principal.getLang();
            LocaleContextHolder.setLocale(new Locale(lang));
        }

        return true;
    }
}
