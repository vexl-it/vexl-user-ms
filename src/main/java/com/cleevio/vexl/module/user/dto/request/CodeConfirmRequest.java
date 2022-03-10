package com.cleevio.vexl.module.user.dto.request;

import com.cleevio.vexl.module.user.serializer.Base64Deserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeConfirmRequest {

    @NotNull
    @Schema(required = true, description = "ID of verification. You should get it in phone verification endpoint.")
    private Long id;

    @NotBlank
    @Schema(required = true, description = "Code from SMS for verification.")
    private String code;

    @NotNull
    @Schema(required = true, description = "Base64 encoded user's public_key")
    @JsonDeserialize(using = Base64Deserializer.class)
    private byte[] userPublicKey;
}
