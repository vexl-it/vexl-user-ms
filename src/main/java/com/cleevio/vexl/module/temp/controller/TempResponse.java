package com.cleevio.vexl.module.temp.controller;

import com.cleevio.vexl.utils.EncryptionUtils;
import lombok.Data;

import java.security.KeyPair;

@Data
public class TempResponse {

    private String privateKey;
    private String publicKey;

    public TempResponse(KeyPair keyPair) {
        this.privateKey = EncryptionUtils.encodeToBase64String(keyPair.getPrivate().getEncoded());
        this.publicKey = EncryptionUtils.encodeToBase64String(keyPair.getPublic().getEncoded());
    }
}
