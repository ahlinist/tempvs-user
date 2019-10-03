package club.tempvs.user.service;

import club.tempvs.user.domain.EmailVerification;

public interface EmailVerificationService {

    EmailVerification create(String email);

    EmailVerification get(String id);

    void delete(EmailVerification emailVerification);

    void cleanupDayBack();
}
