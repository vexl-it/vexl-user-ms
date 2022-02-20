package com.cleevio.vexl.module.user.enums;

public enum AlgorithmEnum {

    EdDSA("Ed25519"),
    SHA256("SHA-256"),
    ECIES("EC"),
    ECDSA("SHA1WithECDSA");

    private final String value;

    AlgorithmEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
