package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.dto.request.CodeConfirmRequest;
import com.cleevio.vexl.module.user.dto.response.ConfirmCodeResponse;
import com.cleevio.vexl.module.user.utils.EncryptionUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;


@Service
@Slf4j
@AllArgsConstructor
public class SignatureService {

    private static final String EdDSA = "Ed25519";
    private static final String SHA256 = "SHA-256";

    //TODO private_key will be in keystore and public_key will be in properties
    private static final String publicKey = "MCowBQYDK2VwAyEAUrB4CUnNldgBuC7vuhhCdfuAGzy6YSA5RnkCABa29DE=";
    private static final String privateKey = "MC4CAQAwBQYDK2VwBCIEIIp2wWL1uO8lt+lOXfbI+6Ge0pUCSegkiC7GNRgmG9Lk";

    public ConfirmCodeResponse createSignature(CodeConfirmRequest signatureRequest, String phoneNumber)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, InvalidKeySpecException {
        log.info("Creating digital signature for {}",
                signatureRequest.getUserPublicKey());

        Signature signature = Signature.getInstance(EdDSA);
        signature.initSign(EncryptionUtils.createPrivateKey(privateKey, EdDSA));

        byte[] phoneHashByte = EncryptionUtils.createHash(phoneNumber, SHA256);

        signature.update(
                concatenateHashes(
                        EncryptionUtils.decodeBase64String(signatureRequest.getUserPublicKey()),
                        phoneHashByte
                )
        );

        byte[] digitalSignature = signature.sign();

        log.info("Digital signature is done.");

        return ConfirmCodeResponse
                .builder()
                .publicKey(signatureRequest.getUserPublicKey())
                .phoneHash(EncryptionUtils.encodeToBase64String(phoneHashByte))
                .signature(EncryptionUtils.encodeToBase64String(digitalSignature))
                .valid(true)
                .build();
    }

    private byte[] concatenateHashes(byte[] publicKey, byte[] phoneHash) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(publicKey);
        outputStream.write(phoneHash);

        return outputStream.toByteArray();
    }

    public boolean isValid(String publicKey, String phoneHash, String digitalSignature)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
        byte[] concatenateHashes = concatenateHashes(EncryptionUtils.decodeBase64String(publicKey), EncryptionUtils.decodeBase64String(phoneHash));
        return isValid(EncryptionUtils.encodeToBase64String(concatenateHashes), digitalSignature);
    }

    public boolean isValid(String concatenateHashes, String digitalSignature)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(EdDSA);
        signature.initVerify(EncryptionUtils.createPublicKey(publicKey, EdDSA));
        signature.update(EncryptionUtils.decodeBase64String(concatenateHashes));
        return signature.verify(EncryptionUtils.decodeBase64String(digitalSignature));
    }

}
