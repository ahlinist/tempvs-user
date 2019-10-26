package club.tempvs.user.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.user.component.EmailSender;
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

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class EmailVerificationServiceTest {

    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    @Mock
    private EmailSender emailSender;
    @Mock
    private UserDao userDao;
    @Mock
    private EmailVerificationDao emailVerificationDao;

    @Mock
    private EmailVerification emailVerification;
    @Mock
    private User user;

    @Test
    public void testCreate() {
        String email = "test@email.com";

        when(emailVerificationDao.save(any(EmailVerification.class))).thenReturn(emailVerification);

        EmailVerification result = emailVerificationService.create(email);

        verify(emailSender).sendRegistrationVerification(eq(email), anyString());
        verify(emailVerificationDao).save(any(EmailVerification.class));
        verifyNoMoreInteractions(emailVerificationDao, emailSender);

        assertEquals("Email verification object is returned", emailVerification, result);
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void testCreateForExisting() {
        String email = "test@email.com";
        Optional<User> userOptional = Optional.of(user);

        when(userDao.get(email)).thenReturn(userOptional);

        emailVerificationService.create(email);
    }
}
