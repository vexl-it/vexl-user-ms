package com.cleevio.vexl.module.user.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChallengeRequest {

    @NotBlank
    private final String userPublicKey;

    @NotBlank
    private final String signature;
}
