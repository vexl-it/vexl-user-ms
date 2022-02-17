package com.cleevio.vexl.module.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class PhoneConfirmRequest {

    @Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$")
    @NotBlank
    @Schema(required = true, description = "Phone must be valid according industry-standard notation pattern specified by ITU-T E.123")
    private String phoneNumber;
}
