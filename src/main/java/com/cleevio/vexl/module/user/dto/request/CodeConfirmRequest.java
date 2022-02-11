package com.cleevio.vexl.module.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CodeConfirmRequest {

    @NotNull
    @Schema(required = true)
    private Long id;

    @NotNull
    @Schema(required = true)
    private String code;

    @NotNull
    @Schema(required = true)
    private String userPublicKey;
}
