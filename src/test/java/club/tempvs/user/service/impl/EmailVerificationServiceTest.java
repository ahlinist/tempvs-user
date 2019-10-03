package club.tempvs.user.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.user.component.EmailSender;
import club.tempvs.user.dao.EmailVerificationRepository;
import club.tempvs.user.domain.EmailVerification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.NoSuchElementException;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class EmailVerificationServiceTest {

    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    @Mock
    private EmailVerificationRepository emailVerificationRepository;
    @Mock
    private EmailSender emailSender;

    @Mock
    private EmailVerification emailVerification;

    @Test
    public void testCreate() {
        String email = "test@email.com";

        when(emailVerificationRepository.save(any(EmailVerification.class))).thenReturn(emailVerification);

        EmailVerification result = emailVerificationService.create(email);

        verify(emailSender).sendRegistrationVerification(eq(email), anyString());
        verify(emailVerificationRepository).save(any(EmailVerification.class));
        verifyNoMoreInteractions(emailVerificationRepository, emailSender);

        assertEquals("Email verification object is returned", emailVerification, result);
    }

    @Test
    public void testGet() {
        String id = "verification id";

        when(emailVerificationRepository.findByVerificationId(id)).thenReturn(Optional.of(emailVerification));

        EmailVerification result = emailVerificationService.get(id);

        verify(emailVerificationRepository).findByVerificationId(id);
        verifyNoMoreInteractions(emailVerificationRepository);

        assertEquals("Email verification is returned", emailVerification, result);
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetForMissing() {
        String id = "verification id";

        when(emailVerificationRepository.findByVerificationId(id)).thenReturn(Optional.empty());

        emailVerificationService.get(id);
    }

    @Test
    public void testDelete() {
        emailVerificationService.delete(emailVerification);

        verify(emailVerificationRepository).delete(emailVerification);
        verifyNoMoreInteractions(emailVerificationRepository);
    }
}
