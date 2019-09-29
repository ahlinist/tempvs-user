package club.tempvs.user.service.impl;

import club.tempvs.user.dao.EmailVerificationRepository;
import club.tempvs.user.domain.EmailVerification;
import club.tempvs.user.exception.VerificationAlreadyExistsException;
import club.tempvs.user.service.EmailVerificationService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final String DIGEST_ALGORITHM = "MD5";

    private final EmailVerificationRepository emailVerificationRepository;

    @Override
    @SneakyThrows
    public EmailVerification create(String email) {
        if (find(email).isPresent()) {
            throw new VerificationAlreadyExistsException();
        }

        String verificationSequence = email + Instant.now().toEpochMilli();
        MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
        digest.update(verificationSequence.getBytes());
        String verificationId = DatatypeConverter.printHexBinary(digest.digest());

        //TODO: send an email (use amqp)
        EmailVerification verification = new EmailVerification(email, verificationId);
        return save(verification);
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
