package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.dto.request.CodeConfirmRequest;
import com.cleevio.vexl.module.user.dto.response.ConfirmCodeResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


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
        signature.initSign(createPrivateKey(privateKey));

        byte[] publicKeyPhoneHashConcatenation = concatenateHashes(
                decodeBase64String(signatureRequest.getUserPublicKey()),
                createHash(phoneNumber, SHA256)
        );
        signature.update(publicKeyPhoneHashConcatenation);

        byte[] digitalSignature = signature.sign();

        log.info("Digital signature is done.");

        return ConfirmCodeResponse
                .builder()
                .publicKeyPhoneHash(encodeToBase64String(publicKeyPhoneHashConcatenation))
                .signature(encodeToBase64String(digitalSignature))
                .valid(true)
                .build();
    }


    private String encodeToBase64String(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
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

    private PrivateKey createPrivateKey(String base64PrivateKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedPrivateBytes = Base64.getDecoder().decode(base64PrivateKey);
        return KeyFactory.getInstance(EdDSA).generatePrivate(new PKCS8EncodedKeySpec(decodedPrivateBytes));
    }

    private PublicKey createPublicKey(String base64PublicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedPublicBytes = Base64.getDecoder().decode(base64PublicKey);
        return KeyFactory.getInstance(EdDSA).generatePublic(new X509EncodedKeySpec(decodedPublicBytes));
    }


    public boolean isValid(String concatenateHashes, String digitalSignature)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(EdDSA);
        signature.initVerify(createPublicKey(publicKey));
        signature.update(decodeBase64String(concatenateHashes));
        return signature.verify(decodeBase64String(digitalSignature));
    }

    private byte[] decodeBase64String(String value) {
        return Base64.getDecoder().decode(value);
    }

}
