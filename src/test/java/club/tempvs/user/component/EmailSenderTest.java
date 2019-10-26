package club.tempvs.user.component;

import club.tempvs.user.amqp.EmailEventProcessor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.Locale;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmailSenderTest {

    private static final Locale locale = Locale.ENGLISH;

    @InjectMocks
    private EmailSender emailSender;

    @Mock
    private MessageSource messageSource;
    @Mock
    private EmailBuilder emailBuilder;
    @Mock
    private EmailEventProcessor emailEventProcessor;

    @Mock
    private MessageChannel messageChannel;

    @BeforeClass
    public static void setUp() {
        LocaleContextHolder.setLocale(locale);
    }

    @Test
    public void testSendRegistrationVerification() {
        String email = "test@email.com";
        String subject = "user.registration.email.subject";
        String bodyMessage = "user.registration.email.body.message";
        String buttonText = "user.registration.email.body.button";
        String baseUrl = "http://localhost:8080";
        emailSender.setBaseUrl(baseUrl);
        String verificationId = "verificationId";
        String link = baseUrl + "/user/registration/" + verificationId;
        String emailBody = "body";

        when(messageSource.getMessage(subject, null, subject, locale)).thenReturn(subject);
        when(messageSource.getMessage(bodyMessage, null, bodyMessage, locale)).thenReturn(bodyMessage);
        when(messageSource.getMessage(buttonText, null, buttonText, locale)).thenReturn(buttonText);
        when(emailBuilder.buildRegistrationVerification(bodyMessage, link, buttonText)).thenReturn(emailBody);
        when(emailEventProcessor.send()).thenReturn(messageChannel);

        emailSender.sendRegistrationVerification(email, verificationId);

        verify(emailBuilder).buildRegistrationVerification(bodyMessage, link, buttonText);
        verify(messageChannel).send(any(Message.class));
        verify(emailEventProcessor).send();
        verifyNoMoreInteractions(messageChannel);
    }
}
