package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.dto.response.SignatureResponse;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.VerificationNotFoundException;
import com.cleevio.vexl.module.user.exception.DigitalSignatureException;
import com.cleevio.vexl.utils.EncryptionUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;


@Service
@Slf4j
@AllArgsConstructor
public class SignatureService {

    @Value("${signature.user.publickey}")
    private final String publicKey;

    //TODO private_key will be in keystore and public_key will be in properties
    private static final String privateKey = "MC4CAQAwBQYDK2VwBCIEIIp2wWL1uO8lt+lOXfbI+6Ge0pUCSegkiC7GNRgmG9Lk";

    public SignatureResponse createSignature(User user, String algorithm)
            throws DigitalSignatureException, VerificationNotFoundException {

        log.info("Creating digital signature for {}",
                user.getPublicKey());

        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(EncryptionUtils.createPrivateKey(privateKey, algorithm));

            if (user.getUserVerification() == null || user.getUserVerification().getPhoneNumber() == null) {
                throw new VerificationNotFoundException();
            }

            String phoneHash = user.getUserVerification().getPhoneNumber();

            signature.update(
                    joinBytes(
                            EncryptionUtils.decodeBase64String(user.getPublicKey()),
                            EncryptionUtils.decodeBase64String(phoneHash)
                    )
            );

            byte[] digitalSignature = signature.sign();

            log.info("Digital signature is done.");

            return new SignatureResponse(phoneHash,
                    EncryptionUtils.encodeToBase64String(digitalSignature),
                    true);

        } catch (NoSuchAlgorithmException | InvalidKeyException | IOException | InvalidKeySpecException | SignatureException e) {
            log.error("Error occurred while creating signature.");
            throw new DigitalSignatureException();
        }
    }

    private byte[] joinBytes(byte[] publicKey, byte[] phoneHash) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(publicKey);
        outputStream.write(phoneHash);

        return outputStream.toByteArray();
    }

    public boolean isSignatureValid(String publicKey, String phoneHash, String digitalSignature, String signatureAlgorithm, String publicKeyAlgorithm)
            throws DigitalSignatureException, IOException {
        byte[] valueForSign = joinBytes(EncryptionUtils.decodeBase64String(publicKey), EncryptionUtils.decodeBase64String(phoneHash));
        return isSignatureValid(valueForSign, digitalSignature, this.publicKey, signatureAlgorithm, publicKeyAlgorithm);
    }

    public boolean isSignatureValid(byte[] valueForSign, String digitalSignature, String publicKey, String signatureAlgorithm, String publicKeyAlgorithm)
            throws DigitalSignatureException {
        try {
            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initVerify(EncryptionUtils.createPublicKey(publicKey, publicKeyAlgorithm));
            signature.update(valueForSign);
            return signature.verify(EncryptionUtils.decodeBase64String(digitalSignature));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            log.error("Error occurred while verifying signature {}, error {}",
                    digitalSignature,
                    e.getMessage());
            throw new DigitalSignatureException();
        }

    }

}
