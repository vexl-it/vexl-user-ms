package com.cleevio.vexl.module.user.dto.response;

import com.cleevio.vexl.module.user.entity.UserVerification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.annotation.Nullable;

@Data
public class ConfirmCodeResponse {

    @Nullable
    @Schema(description = "Challenge for user. It is used to verify that the public key is really his.")
    private final String challenge;

    @Schema(description = "Boolean whether is phone verified.")
    private final boolean phoneVerified;

    public ConfirmCodeResponse(UserVerification challengeVerification) {
        this.challenge = challengeVerification.getChallenge();
        this.phoneVerified = challengeVerification.isPhoneVerified();
    }
}
