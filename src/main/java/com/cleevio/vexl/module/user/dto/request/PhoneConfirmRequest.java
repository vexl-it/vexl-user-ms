package com.cleevio.vexl.module.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class PhoneConfirmRequest {

    //pattern according industry-standard notation specified by ITU-T E.123
    @Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$")
    @NotBlank
    @Schema(required = true)
    private String phoneNumber;
}
