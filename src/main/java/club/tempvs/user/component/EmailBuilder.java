package club.tempvs.user.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class EmailBuilder {

    private final ITemplateEngine templateEngine;

    public String buildRegistrationVerification(String message, String link, String buttonText) {
        Context context = new Context();
        context.setVariable("message", message);
        context.setVariable("link", link);
        context.setVariable("buttonText", buttonText);
        return templateEngine.process("registrationVerificationTemplate", context);
    }
}
