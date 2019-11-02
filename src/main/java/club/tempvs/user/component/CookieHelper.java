package club.tempvs.user.component;

import club.tempvs.user.domain.User;
import club.tempvs.user.dto.AuthCookieDto;
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
    private static final String LOGGED_IN_COOKIE_NAME = "TEMPVS_LOGGED_IN";
    private static final String LOGGED_IN_COOKIE_VALUE = "true";
    private static final String COOKIE_ROOT_PATH = "/";

    private final ConversionService mvcConversionService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public Cookie buildAuthCookie(User user) {
        AuthCookieDto authCookie = mvcConversionService.convert(user, AuthCookieDto.class);
        String stringCookie = objectMapper.writeValueAsString(authCookie);
        String encodedCookie = Base64.getEncoder().encodeToString(stringCookie.getBytes());
        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, encodedCookie);
        cookie.setPath(COOKIE_ROOT_PATH);
        cookie.setMaxAge(Integer.MAX_VALUE);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public Cookie buildLoggedInCookie() {
        Cookie cookie = new Cookie(LOGGED_IN_COOKIE_NAME, LOGGED_IN_COOKIE_VALUE);
        cookie.setPath(COOKIE_ROOT_PATH);
        cookie.setMaxAge(Integer.MAX_VALUE);
        cookie.setHttpOnly(false);
        return cookie;
    }
}
