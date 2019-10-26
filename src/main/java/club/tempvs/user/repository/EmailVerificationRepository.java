package club.tempvs.user.repository;

import club.tempvs.user.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByVerificationId(String verificationId);

    @Modifying
    @Query("DELETE from EmailVerification e where e.createdDate <= :retentionTime")
    void cleanupDayBack(Instant retentionTime);
}
