package club.tempvs.user.service.impl;

import club.tempvs.user.dao.EmailVerificationDao;
import club.tempvs.user.dao.UserDao;
import club.tempvs.user.domain.EmailVerification;
import club.tempvs.user.domain.User;
import club.tempvs.user.exception.UserAlreadyExistsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
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
        when(bCryptPasswordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userDao.save(preparedUser)).thenReturn(user);

        User result = userService.register(verificationId, password);

        verify(emailVerificationDao).get(verificationId);
        verify(emailVerificationDao).delete(emailVerification);
        verify(userDao).get(email);
        verify(bCryptPasswordEncoder).encode(password);
        verify(userDao).save(preparedUser);
        verifyNoMoreInteractions(bCryptPasswordEncoder, userDao);

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
    public void testGetUser() {
        String email = "email@test.com";
        Optional<User> userOptional = Optional.of(user);

        when(userDao.get(email)).thenReturn(userOptional);

        Optional<User> result = userDao.get(email);

        verify(userDao).get(email);
        verifyNoMoreInteractions(userDao);

        assertEquals("User object is returned", userOptional, result);
    }
}
