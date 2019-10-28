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
    private static final String AUTH_COOKIE_PATH = "/";

    private final ConversionService mvcConversionService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public Cookie buildAuthCookie(User user) {
        AuthCookieDto authCookie = mvcConversionService.convert(user, AuthCookieDto.class);
        String stringCookie = objectMapper.writeValueAsString(authCookie);
        String encodedCookie = Base64.getEncoder().encodeToString(stringCookie.getBytes());
        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, encodedCookie);
        cookie.setHttpOnly(true);
        cookie.setPath(AUTH_COOKIE_PATH);
        return cookie;
    }
}
