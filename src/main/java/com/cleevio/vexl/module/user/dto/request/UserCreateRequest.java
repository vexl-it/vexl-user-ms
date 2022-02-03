package com.cleevio.vexl.module.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserCreateRequest {

    @NotBlank
    @Schema(required = true)
    private String username;

    @Schema(description = "Base64 encoded file data including header. i.e.: data:image/png;base64,iVBORw0KGgo")
    private String avatar;

    @NotBlank
    @Schema(required = true)
    private String publicKey;
}
