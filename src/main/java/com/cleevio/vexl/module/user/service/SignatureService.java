package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.dto.request.SignatureRequest;
import com.cleevio.vexl.module.user.dto.response.SignatureResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Base64;


@Service
@Slf4j
@AllArgsConstructor
public class SignatureService {

    private static final String EdDSA = "Ed25519";
    private static final String SHA256 = "SHA-256";

    public SignatureResponse createSignature(SignatureRequest signatureRequest)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        log.info("Creating digital signature for {}",
                signatureRequest.getPublicKey());

        Signature signature = Signature.getInstance(EdDSA);
        KeyPair keyPair = retrieveKeyPair();
        signature.initSign(keyPair.getPrivate());

        byte[] publicKeyPhoneHashConcatenation = concatenateHashes(
                signatureRequest.getPublicKey(),
                createHash(signatureRequest.getPhoneNumber(), SHA256)
        );
        signature.update(publicKeyPhoneHashConcatenation);

        byte[] digitalSignature = signature.sign();

        log.info("Digital signature is done.");

        boolean isValid = isValid(digitalSignature, signatureRequest, keyPair.getPublic());

        return SignatureResponse
                .builder()
                .publicKeyPhoneHashConcatenation(publicKeyPhoneHashConcatenation)
                .signature(digitalSignature)
                .valid(isValid)
                .build();
    }

    private boolean isValid(byte[] digitalSignature, SignatureRequest signatureRequest, PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        Signature signature = Signature.getInstance(EdDSA);
        signature.initVerify(publicKey);
        byte[] concatenateHashes = concatenateHashes(
                signatureRequest.getPublicKey(),
                createHash(signatureRequest.getPhoneNumber(), SHA256)
        );
        signature.update(concatenateHashes);
        return signature.verify(digitalSignature);
    }

    private byte[] concatenateHashes(byte[] publicKey, byte[] phoneHash) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(publicKey);
        outputStream.write(phoneHash);

        return outputStream.toByteArray();
    }

    private byte[] createHash(String phoneNumber, String hashFunction)
            throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(hashFunction);
        return digest.digest(phoneNumber.getBytes(StandardCharsets.UTF_8));
    }

    private KeyPair retrieveKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(EdDSA);
        return kpg.generateKeyPair();
    }
}
