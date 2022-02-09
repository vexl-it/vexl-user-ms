package com.cleevio.vexl.module.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignatureResponse {

    byte[] publicKeyPhoneHashConcatenation;
    byte[] signature;
    boolean valid;

}
