package com.cleevio.vexl.module.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmCodeResponse {

    String publicKeyPhoneHash;
    String signature;
    boolean valid;

}
