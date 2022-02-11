package com.cleevio.vexl.module.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignatureRequest {

    String publicKey;
    String phoneNumber;

}
