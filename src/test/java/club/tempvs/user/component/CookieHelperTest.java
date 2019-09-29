package club.tempvs.user.component;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.user.domain.User;
import club.tempvs.user.dto.TempvsPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import javax.servlet.http.Cookie;

@RunWith(MockitoJUnitRunner.class)
public class CookieHelperTest {

    private static final String AUTH_COOKIE_NAME = "TEMPVS_AUTH";

    @InjectMocks
    private CookieHelper cookieHelper;

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ConversionService conversionService;

    @Mock
    private User user;
    @Mock
    private TempvsPrincipal principal;

    @Test
    public void testBuildAuthCookie() throws Exception {
        String stringPrincipal = "string principal";
        String encodedPrincipal = "c3RyaW5nIHByaW5jaXBhbA==";

        when(conversionService.convert(user, TempvsPrincipal.class)).thenReturn(principal);
        when(objectMapper.writeValueAsString(principal)).thenReturn(stringPrincipal);

        Cookie result = cookieHelper.buildAuthCookie(user);

        verify(conversionService).convert(user, TempvsPrincipal.class);
        verify(objectMapper).writeValueAsString(principal);
        verifyNoMoreInteractions(conversionService, objectMapper);

        assertEquals("", encodedPrincipal, result.getValue());
        assertTrue("The cookie is http only", result.isHttpOnly());
        assertEquals("Result cookie has correct name", AUTH_COOKIE_NAME, result.getName());
    }
}
