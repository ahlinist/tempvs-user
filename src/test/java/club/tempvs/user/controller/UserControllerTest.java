package club.tempvs.user.controller;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.user.component.CookieHelper;
import club.tempvs.user.domain.User;
import club.tempvs.user.dto.RegisterDto;
import club.tempvs.user.dto.UserDto;
import club.tempvs.user.service.EmailVerificationService;
import club.tempvs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;
    @Mock
    private ConversionService mvcConversionService;
    @Mock
    private CookieHelper cookieHelper;
    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private UserDto userDto;
    @Mock
    private User user;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private Cookie cookie;
    @Mock
    private RegisterDto registerDto;

    @Test
    public void testVerify() {
        String verificationId = "verification id";
        String password = "password";

        when(registerDto.getPassword()).thenReturn(password);
        when(userService.register(verificationId, password)).thenReturn(user);
        when(mvcConversionService.convert(user, UserDto.class)).thenReturn(userDto);
        when(cookieHelper.buildAuthCookie(user)).thenReturn(cookie);

        UserDto result = userController.verify(verificationId, registerDto, httpServletResponse);

        verify(userService).register(verificationId, password);
        verify(mvcConversionService).convert(user, UserDto.class);
        verify(httpServletResponse).addCookie(cookie);
        verifyNoMoreInteractions(userService, mvcConversionService, httpServletResponse);

        assertEquals("UserDto is returned", userDto, result);
    }

    @Test
    public void testRegister() {
        String email = "test@email.com";

        when(registerDto.getEmail()).thenReturn(email);

        userController.register(registerDto);

        verify(emailVerificationService).create(email);
        verifyNoMoreInteractions(emailVerificationService);
    }
}
