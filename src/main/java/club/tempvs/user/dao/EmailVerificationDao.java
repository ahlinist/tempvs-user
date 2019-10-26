package club.tempvs.user.dao;

import club.tempvs.user.domain.EmailVerification;

import java.util.Optional;

public interface EmailVerificationDao {

    Optional<EmailVerification> get(String id);

    void delete(EmailVerification emailVerification);

    EmailVerification save(EmailVerification emailVerification);
}
