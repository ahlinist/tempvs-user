package club.tempvs.user.job;

import club.tempvs.user.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationCleanupJob {

    private static final long ONE_HOUR_IN_MS = 60 * 60 * 1000;

    private final EmailVerificationService emailVerificationService;

    @Scheduled(fixedRate = ONE_HOUR_IN_MS, initialDelay = ONE_HOUR_IN_MS)
    public void execute() {
        emailVerificationService.cleanupDayBack();
    }
}
