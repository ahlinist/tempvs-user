package club.tempvs.user.controller;

import static org.mockito.Mockito.*;

import club.tempvs.user.domain.User;
import club.tempvs.user.dto.CredentialsDto;
import club.tempvs.user.service.EmailVerificationService;
import club.tempvs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private static final String REFRESH_COOKIES_HEADER = "Tempvs-Refresh-Cookies";
    private static final String LOGOUT_HEADER = "Tempvs-Logout";

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;
    @Mock
    private ConversionService mvcConversionService;
    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private User user;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private CredentialsDto credentialsDto;

    @Test
    public void testVerify() {
        String verificationId = "verification id";
        String password = "password";
        String userInfo = "user info";

        when(credentialsDto.getPassword()).thenReturn(password);
        when(userService.register(verificationId, password)).thenReturn(user);
        when(mvcConversionService.convert(user, String.class)).thenReturn(userInfo);

        userController.verify(verificationId, credentialsDto, httpServletResponse);

        verify(userService).register(verificationId, password);
        verify(mvcConversionService).convert(user, String.class);
        verify(httpServletResponse).addHeader(REFRESH_COOKIES_HEADER, userInfo);
        verifyNoMoreInteractions(userService, mvcConversionService, httpServletResponse);
    }

    @Test
    public void testRegister() {
        String email = "test@email.com";

        when(credentialsDto.getEmail()).thenReturn(email);

        userController.register(credentialsDto);

        verify(emailVerificationService).create(email);
        verifyNoMoreInteractions(emailVerificationService);
    }

    @Test
    public void testLogin() {
        String email = "email@test.com";
        String password = "password";
        String userInfo = "user info";
        String referer = "https://my-host.com/1";

        when(credentialsDto.getEmail()).thenReturn(email);
        when(credentialsDto.getPassword()).thenReturn(password);
        when(userService.login(email, password)).thenReturn(user);
        when(httpServletRequest.getHeader("Referer")).thenReturn(referer);
        when(mvcConversionService.convert(user, String.class)).thenReturn(userInfo);

        userController.login(credentialsDto, httpServletRequest, httpServletResponse);

        verify(userService).login(email, password);
        verify(httpServletRequest).getHeader("Referer");
        verify(httpServletResponse).addHeader(REFRESH_COOKIES_HEADER, userInfo);
        verify(httpServletResponse).addHeader("Location", "https://my-host.com/1");
        verifyNoMoreInteractions(userService, httpServletRequest, httpServletResponse);
    }

    @Test
    public void testLogout() {
        userController.logout(httpServletResponse);

        verify(httpServletResponse).addHeader(LOGOUT_HEADER, "");
        verifyNoMoreInteractions(httpServletResponse);
    }
}
