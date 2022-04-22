package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.dto.response.SignatureResponse;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.InvalidPublicKeyAndHashException;
import com.cleevio.vexl.module.user.exception.VerificationNotFoundException;
import com.cleevio.vexl.module.user.exception.DigitalSignatureException;
import com.cleevio.vexl.utils.EncryptionUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

/**
 * Creating and verifying signatures.
 *
 * If the user verifies his challenge, his public key is verified,
 * and we can generate a certificate for him - a signed combination of
 * his public key and a hash of his phone number or facebookId.
 */
@Service
@Slf4j
@AllArgsConstructor
public class SignatureService {

    @Value("${signature.user.publickey}")
    private final String publicKey;

    //TODO private_key will be in keystore and public_key will be in properties
    private static final String privateKey = "MC4CAQAwBQYDK2VwBCIEIIp2wWL1uO8lt+lOXfbI+6Ge0pUCSegkiC7GNRgmG9Lk";

    @Transactional(readOnly = true)
    public SignatureResponse createSignature(User user, String algorithm)
            throws VerificationNotFoundException, DigitalSignatureException, InvalidPublicKeyAndHashException {
        log.info("Creating digital signature for user {}",
                user.getId());

        if (user.getUserVerification() == null || user.getUserVerification().getPhoneNumber() == null) {
            log.error("Verification is missing. We need verification for phone number.");
            throw new VerificationNotFoundException();
        }

        return createSignature(
                user.getPublicKey(),
                user.getUserVerification().getPhoneNumber(),
                algorithm
        );
    }

    @Transactional(readOnly = true)
    public SignatureResponse createSignature(byte[] publicKey, byte[] hash, String algorithm)
            throws DigitalSignatureException, InvalidPublicKeyAndHashException {

        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(EncryptionUtils.createPrivateKey(privateKey, algorithm));

            signature.update(
                    joinBytes(
                            publicKey,
                            hash
                    )
            );

            byte[] digitalSignature = signature.sign();

            log.info("Digital signature is done.");

            return new SignatureResponse(
                    hash,
                    digitalSignature,
                    true
            );

        } catch (IOException e) {
            log.error("Error occurred while joining bytes of public key and hash.", e);
            throw new InvalidPublicKeyAndHashException();
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | SignatureException e) {
            log.error("Error occurred while creating signature.", e);
            throw new DigitalSignatureException();
        }
    }

    private byte[] joinBytes(byte[] publicKey, byte[] phoneHash) throws IOException {
        log.info("Joining public key and hash.");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(publicKey);
        outputStream.write(phoneHash);

        return outputStream.toByteArray();
    }

    public boolean isSignatureValid(String publicKey, String phoneHash, String digitalSignature, String signatureAlgorithm, String publicKeyAlgorithm)
            throws DigitalSignatureException, IOException {
        byte[] valueForSign = joinBytes(EncryptionUtils.decodeBase64String(publicKey), EncryptionUtils.decodeBase64String(phoneHash));
        return isSignatureValid(valueForSign,
                EncryptionUtils.decodeBase64String(digitalSignature),
                EncryptionUtils.decodeBase64String(this.publicKey),
                signatureAlgorithm,
                publicKeyAlgorithm
        );
    }

    public boolean isSignatureValid(byte[] valueForSign, byte[] digitalSignature, byte[] publicKey, String signatureAlgorithm, String publicKeyAlgorithm)
            throws DigitalSignatureException {
        try {
            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initVerify(EncryptionUtils.createPublicKey(publicKey, publicKeyAlgorithm));
            signature.update(valueForSign);
            return signature.verify(digitalSignature);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            log.error("Error occurred while verifying signature {}, error {}",
                    digitalSignature,
                    e.getMessage());
            throw new DigitalSignatureException();
        }

    }

}
