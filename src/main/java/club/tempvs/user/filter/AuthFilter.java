package club.tempvs.user.filter;

import club.tempvs.user.dto.TempvsPrincipal;
import club.tempvs.user.token.AuthToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class AuthFilter extends GenericFilterBean {

    private static final String USER_INFO_HEADER = "User-Info";

    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String userInfoHeaderValue = httpRequest.getHeader(USER_INFO_HEADER);

        if (!StringUtils.isEmpty(userInfoHeaderValue)) {
            httpResponse.setHeader(USER_INFO_HEADER, userInfoHeaderValue);

            TempvsPrincipal principal = objectMapper.readValue(userInfoHeaderValue, TempvsPrincipal.class);
            Set<String> roles = principal.getRoles();
            Set<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(toSet());
            AuthToken authToken = new AuthToken(principal, authorities);
            SecurityContextHolder.getContext()
                    .setAuthentication(authToken);

            Optional.ofNullable(principal.getLang())
                    .map(Locale::new)
                    .ifPresent(LocaleContextHolder::setLocale);
        }

        chain.doFilter(httpRequest, httpResponse);
    }
}
