package com.cleevio.vexl.module.user.dto;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

public record SignatureData(

        @NotBlank
        @Nullable
        String hash,

        @NotBlank
        @Nullable
        String signature,

        boolean challengeVerified

) {
}
