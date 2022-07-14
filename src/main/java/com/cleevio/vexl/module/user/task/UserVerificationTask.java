package com.cleevio.vexl.module.user.task;

import com.cleevio.vexl.module.user.service.UserVerificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Task for deleting expired verifications.
 * Every 10 minutes we check if there is some expired verification in USER_VERIFICATION table, if so, we delete it.
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserVerificationTask {

    private final UserVerificationService verificationService;

    @Scheduled(fixedDelay = 600_000)
    @Transactional(rollbackFor = Exception.class)
    public void deleteExpiredVerifications() {
        log.info("Deleting expired user verification");
        this.verificationService.deleteExpiredVerifications();
    }
}
