package club.tempvs.user.component;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmailBuilderTest {

    @InjectMocks
    private EmailBuilder emailBuilder;

    @Mock
    private ITemplateEngine templateEngine;

    @Test
    public void testBuildRegistrationVerification() {
        String emailBody = "body";
        String message = "message";
        String link = "www.test.com";
        String buttonText = "text";

        when(templateEngine.process(eq("registrationVerificationTemplate"), any(Context.class))).thenReturn(emailBody);

        String result = emailBuilder.buildRegistrationVerification(message, link, buttonText);

        verify(templateEngine).process(eq("registrationVerificationTemplate"), any(Context.class));
        verifyNoMoreInteractions(templateEngine);

        assertEquals("Email body is returned", emailBody, result);
    }
}
