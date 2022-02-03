package com.cleevio.vexl.module.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CodeConfirmResponse {

    private boolean success;
    private String message;
}
