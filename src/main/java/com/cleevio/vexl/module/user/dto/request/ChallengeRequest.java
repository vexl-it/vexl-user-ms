package com.cleevio.vexl.module.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChallengeRequest {

    @NotBlank
    @Schema(required = true, description = "Base64 encoded user's public_key")
    private final String userPublicKey;

    @NotBlank
    @Schema(required = true, description = "Base64 encoded challenge's signature")
    private final String signature;
}
