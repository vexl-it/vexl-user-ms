package com.cleevio.vexl.module.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignatureResponse {

    public final String phoneHash;
    public final String signature;
    public final boolean challengeValid;

    public SignatureResponse(boolean challengeValid) {
        this.phoneHash = null;
        this.signature = null;
        this.challengeValid = challengeValid;
    }
}
