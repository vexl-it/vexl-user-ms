package com.cleevio.vexl.module.temp.controller;

import com.cleevio.vexl.module.user.dto.response.SignatureResponse;
import com.cleevio.vexl.module.user.enums.AlgorithmEnum;
import com.cleevio.vexl.utils.EncryptionUtils;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@RestController
@RequestMapping(value = "/api/v1/temp")
@AllArgsConstructor
public class TempController {

    /**
     * Will be here till C library will not be done. So FE can use this endpoint for generating of KeyPairs.
     */
    @GetMapping("key-pairs")
    TempResponse requestConfirmPhone() throws NoSuchAlgorithmException {
        return new TempResponse(EncryptionUtils.retrieveKeyPair(AlgorithmEnum.ECIES.getValue()));
    }

    @GetMapping("signature")
    private TempResponse2 signChallenge(@RequestBody TempRequest request) throws SignatureException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {

            Signature signature = Signature.getInstance(AlgorithmEnum.ECDSA.getValue());
            signature.initSign(EncryptionUtils.createPrivateKey(request.getPrivateKey(), AlgorithmEnum.ECIES.getValue()));

            signature.update(Base64.getUrlDecoder().decode(request.getChallenge()));

            byte[] digitalSignature = signature.sign();

            return new TempResponse2(EncryptionUtils.encodeToBase64String(digitalSignature));

    }
}
