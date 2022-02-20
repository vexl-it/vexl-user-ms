package com.cleevio.vexl.module.user.dto.response;

import com.cleevio.vexl.module.user.entity.UserVerification;
import lombok.Data;

@Data
public class ConfirmCodeResponse {

    private final String challenge;

    private final boolean phoneVerified;

    public ConfirmCodeResponse(UserVerification challengeVerification) {
        if (challengeVerification == null) {
            this.challenge = null;
            this.phoneVerified = false;
        } else {
            this.challenge = challengeVerification.getChallenge();
            this.phoneVerified = challengeVerification.isPhoneVerified();
        }
    }
}
