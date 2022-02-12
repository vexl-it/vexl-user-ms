package com.cleevio.vexl.module.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmCodeResponse {

    String publicKey;
    String phoneHash;
    String signature;
    boolean valid;

}
