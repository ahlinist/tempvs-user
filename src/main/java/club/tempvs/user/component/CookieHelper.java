package club.tempvs.user.component;

import club.tempvs.user.domain.User;
import club.tempvs.user.dto.TempvsPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class CookieHelper {

    private static final String AUTH_COOKIE_NAME = "TEMPVS_AUTH";

    private final ConversionService mvcConversionService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public Cookie buildAuthCookie(User user) {
        TempvsPrincipal principal = mvcConversionService.convert(user, TempvsPrincipal.class);
        String stringPrincipal = objectMapper.writeValueAsString(principal);
        String encodedPrincipal = Base64.getEncoder().encodeToString(stringPrincipal.getBytes());
        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, encodedPrincipal);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
