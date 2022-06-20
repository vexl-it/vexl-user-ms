package com.cleevio.vexl.module.user.dto.request;

import com.cleevio.vexl.module.user.serializer.TrimStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public record UsernameAvailableRequest (

    @NotBlank
    @Schema(required = true, description = "Username in String format")
    @JsonDeserialize(using = TrimStringDeserializer.class)
    String username

) {
}
