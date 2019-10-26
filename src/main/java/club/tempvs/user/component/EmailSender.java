package club.tempvs.user.component;

import club.tempvs.user.amqp.EmailEventProcessor;
import club.tempvs.user.dto.EmailDto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class EmailSender {

    private static final String REGISTRATION_SUBJECT = "user.registration.email.subject";
    private static final String REGISTRATION_MESSAGE = "user.registration.email.body.message";
    private static final String REGISTRATION_BUTTON_TEXT = "user.registration.email.body.button";

    private static final String VERIFICATION_URL = "/user/registration/";

    private final MessageSource messageSource;
    private final EmailBuilder emailBuilder;
    private final EmailEventProcessor emailEventProcessor;

    @Setter
    @Value("${app.base-url}")
    private String baseUrl;

    public void sendRegistrationVerification(String email, String verificationId) {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage(REGISTRATION_SUBJECT, null, REGISTRATION_SUBJECT, locale);
        String message = messageSource.getMessage(REGISTRATION_MESSAGE, null, REGISTRATION_MESSAGE, locale);
        String buttonText = messageSource.getMessage(REGISTRATION_BUTTON_TEXT, null, REGISTRATION_BUTTON_TEXT, locale);
        String link = baseUrl + VERIFICATION_URL + verificationId;
        String body = emailBuilder.buildRegistrationVerification(message, link, buttonText);

        EmailDto emailPayload = new EmailDto(email, subject, body);

        emailEventProcessor.send()
                .send(MessageBuilder.withPayload(emailPayload).build());
    }
}
