package com.cleevio.vexl.module.user.dto.response;

import com.cleevio.vexl.module.user.entity.UserVerification;
import lombok.Data;

import java.time.Instant;

@Data
public class PhoneConfirmResponse {

    Long verificationId;
    Instant expirationAt;

    public PhoneConfirmResponse(UserVerification verification) {
        this.verificationId = verification.getId();
        this.expirationAt = verification.getExpirationAt();
    }
}
