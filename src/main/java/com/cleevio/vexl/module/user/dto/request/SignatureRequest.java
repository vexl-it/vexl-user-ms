package com.cleevio.vexl.module.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class SignatureRequest {

    @Schema(required = true)
    @NotBlank
    String publicKey;

    @Schema(required = true)
    @NotBlank
    String phoneNumber;

}
