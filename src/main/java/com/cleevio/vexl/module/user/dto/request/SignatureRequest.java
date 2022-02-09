package com.cleevio.vexl.module.user.dto.request;

import lombok.Data;

@Data
public class SignatureRequest {

    byte[] publicKey;
    String phoneNumber;

}
