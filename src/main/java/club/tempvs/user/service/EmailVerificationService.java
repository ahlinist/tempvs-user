package club.tempvs.user.service;

import club.tempvs.user.domain.EmailVerification;

public interface EmailVerificationService {

    EmailVerification create(String email);

    void cleanupDayBack();
}
