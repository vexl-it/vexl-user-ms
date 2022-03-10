package com.cleevio.vexl.module.user.dto.response;

import com.cleevio.vexl.module.user.entity.UserVerification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Optional;

@Data
public class PhoneConfirmResponse {

    @Schema(description = "Verification ID. You will have to send it with code from sms.")
    Long verificationId;

    @Schema(description = "Time when code is expired. Default lifetime is 30 seconds.")
    @Nullable
    Instant expirationAt;

    public PhoneConfirmResponse(UserVerification verification) {
        this.verificationId = verification.getId();
        this.expirationAt = verification.getExpirationAt();
    }
}
