package com.cleevio.vexl.module.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

public record ChallengeRequest(

        @NotNull
        @Schema(required = true, description = "Base64 encoded user's public_key")
        String userPublicKey,

        @NotNull
        @Schema(required = true, description = "Base64 encoded challenge's signature")
        String signature

) {


}
