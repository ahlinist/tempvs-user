package club.tempvs.user.controller;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.user.component.CookieHelper;
import club.tempvs.user.domain.User;
import club.tempvs.user.dto.UserDto;
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
    private UserDto userDto;
    @Mock
    private User user;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private Cookie cookie;

    @Test
    public void testCreate() {
        String email = "test@email.com";
        String password = "password";

        when(userDto.getEmail()).thenReturn(email);
        when(userDto.getPassword()).thenReturn(password);
        when(userService.register(email, password)).thenReturn(user);
        when(mvcConversionService.convert(user, UserDto.class)).thenReturn(userDto);
        when(cookieHelper.buildAuthCookie(user)).thenReturn(cookie);

        UserDto result = userController.register(userDto, httpServletResponse);

        verify(userService).register(email, password);
        verify(mvcConversionService).convert(user, UserDto.class);
        verify(httpServletResponse).addCookie(cookie);
        verifyNoMoreInteractions(userService, mvcConversionService, httpServletResponse);

        assertEquals("UserDto is returned", userDto, result);
    }
}
