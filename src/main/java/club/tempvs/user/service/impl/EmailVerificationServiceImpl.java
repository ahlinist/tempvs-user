package club.tempvs.user.service.impl;

import club.tempvs.user.component.EmailSender;
import club.tempvs.user.dao.EmailVerificationRepository;
import club.tempvs.user.domain.EmailVerification;
import club.tempvs.user.service.EmailVerificationService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final String DIGEST_ALGORITHM = "MD5";

    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailSender emailSender;

    @Override
    @SneakyThrows
    public EmailVerification create(String email) {
        String verificationSequence = email + Instant.now().toEpochMilli();
        MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
        digest.update(verificationSequence.getBytes());
        String verificationId = DatatypeConverter.printHexBinary(digest.digest());

        emailSender.sendRegistrationVerification(email, verificationId);
        EmailVerification verification = new EmailVerification(email, verificationId);
        return save(verification);
    }

    @Override
    @Transactional
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public void cleanupDayBack() {
        Duration day = Duration.ofDays(1);
        Instant retentionTime = Instant.now().minus(day);
        emailVerificationRepository.cleanupDayBack(retentionTime);
    }

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public EmailVerification get(String id) {
        return emailVerificationRepository.findByVerificationId(id).get();
    }

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public void delete(EmailVerification emailVerification) {
        emailVerificationRepository.delete(emailVerification);
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    private EmailVerification save(EmailVerification emailVerification) {
        return emailVerificationRepository.save(emailVerification);
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    private Optional<EmailVerification> find(String email) {
        return emailVerificationRepository.findByEmailIgnoreCase(email);
    }
}
