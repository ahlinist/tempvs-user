package club.tempvs.user.service.impl;

import club.tempvs.user.dao.EmailVerificationDao;
import club.tempvs.user.dao.UserDao;
import club.tempvs.user.domain.EmailVerification;
import club.tempvs.user.domain.User;
import club.tempvs.user.exception.UnauthorizedException;
import club.tempvs.user.exception.UserAlreadyExistsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailVerificationDao emailVerificationDao;
    @Mock
    private UserDao userDao;

    @Mock
    private User user;
    @Mock
    private EmailVerification emailVerification;

    @Test
    public void testRegister() {
        String verificationId = "verification id";
        String email = "test@email.com";
        String password = "password";
        String encodedPassword = "encoded password";
        User preparedUser = new User(email, encodedPassword);
        Optional<EmailVerification> emailVerificationOptional = Optional.of(emailVerification);

        when(emailVerificationDao.get(verificationId)).thenReturn(emailVerificationOptional);
        when(emailVerification.getEmail()).thenReturn(email);
        when(userDao.get(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userDao.save(preparedUser)).thenReturn(user);

        User result = userService.register(verificationId, password);

        verify(emailVerificationDao).get(verificationId);
        verify(emailVerificationDao).delete(emailVerification);
        verify(userDao).get(email);
        verify(passwordEncoder).encode(password);
        verify(userDao).save(preparedUser);
        verifyNoMoreInteractions(passwordEncoder, userDao);

        assertEquals("User object is returned", user, result);
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void testRegisterForDuplicate() {
        String verificationId = "verification id";
        String email = "test@email.com";
        String password = "password";
        Optional<EmailVerification> emailVerificationOptional = Optional.of(emailVerification);

        when(emailVerificationDao.get(verificationId)).thenReturn(emailVerificationOptional);
        when(emailVerification.getEmail()).thenReturn(email);
        when(userDao.get(email)).thenReturn(Optional.of(user));

        userService.register(verificationId, password);
    }

    @Test
    public void testLogin() {
        String email = "email@test.com";
        String password = "password";
        String encodedPassword = "encodedPassword";
        Optional<User> userOptional = Optional.of(user);

        when(userDao.get(email)).thenReturn(userOptional);
        when(user.getPassword()).thenReturn(encodedPassword);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        User result = userService.login(email, password);

        verify(userDao).get(email);
        verify(passwordEncoder).matches(password, encodedPassword);
        verifyNoMoreInteractions(userDao, passwordEncoder);

        assertEquals("User object is returned", user, result);
    }

    @Test(expected = UnauthorizedException.class)
    public void testLoginForWrongCredentials() {
        String email = "email@test.com";
        String password = "password";
        String encodedPassword = "encodedPassword";
        Optional<User> userOptional = Optional.of(user);

        when(userDao.get(email)).thenReturn(userOptional);
        when(user.getPassword()).thenReturn(encodedPassword);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        userService.login(email, password);
    }

    @Test(expected = NoSuchElementException.class)
    public void testLoginForMissingUser() {
        String email = "email@test.com";
        String password = "password";
        Optional<User> userOptional = Optional.empty();

        when(userDao.get(email)).thenReturn(userOptional);

        userService.login(email, password);
    }
}
