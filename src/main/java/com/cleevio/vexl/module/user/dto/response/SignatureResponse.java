package com.cleevio.vexl.module.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignatureResponse {

    public final String hash;
    public final String signature;
    public final boolean challengeValid;

    public SignatureResponse(boolean challengeValid) {
        this.hash = null;
        this.signature = null;
        this.challengeValid = challengeValid;
    }
}
