package club.tempvs.user.dao;

import club.tempvs.user.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByEmailIgnoreCase(String email);

    Optional<EmailVerification> findByVerificationId(String verificationId);
}
