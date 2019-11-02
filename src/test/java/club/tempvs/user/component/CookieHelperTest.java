package club.tempvs.user.component;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.user.domain.User;
import club.tempvs.user.dto.AuthCookieDto;
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

    @InjectMocks
    private CookieHelper cookieHelper;

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ConversionService conversionService;

    @Mock
    private User user;
    @Mock
    private AuthCookieDto authCookieDto;

    @Test
    public void testBuildAuthCookie() throws Exception {
        String stringPrincipal = "string principal";
        String encodedPrincipal = "c3RyaW5nIHByaW5jaXBhbA==";

        when(conversionService.convert(user, AuthCookieDto.class)).thenReturn(authCookieDto);
        when(objectMapper.writeValueAsString(authCookieDto)).thenReturn(stringPrincipal);

        Cookie result = cookieHelper.buildAuthCookie(user);

        verify(conversionService).convert(user, AuthCookieDto.class);
        verify(objectMapper).writeValueAsString(authCookieDto);
        verifyNoMoreInteractions(conversionService, objectMapper);

        assertEquals("Result cookie has encoded principal value", encodedPrincipal, result.getValue());
        assertTrue("The cookie is http only", result.isHttpOnly());
        assertEquals("Result cookie has correct name", "TEMPVS_AUTH", result.getName());
        assertEquals("Result cookie root path", "/", result.getPath());
        assertEquals("Result cookie expires in Integer.MAX_VALUE", Integer.MAX_VALUE, result.getMaxAge());
    }

    @Test
    public void testBuildLoggedInCookie() {
        Cookie result = cookieHelper.buildLoggedInCookie();
        assertEquals("The result cookie has 'true' value", "true", result.getValue());
        assertTrue("The cookie is not http only", !result.isHttpOnly());
        assertEquals("The result cookie has correct name", "TEMPVS_LOGGED_IN", result.getName());
        assertEquals("The result cookie root path", "/", result.getPath());
        assertEquals("Result cookie expires in Integer.MAX_VALUE", Integer.MAX_VALUE, result.getMaxAge());
    }
}
