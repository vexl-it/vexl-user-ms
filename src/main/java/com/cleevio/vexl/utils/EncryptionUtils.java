package com.cleevio.vexl.utils;

import com.cleevio.vexl.module.user.enums.AlgorithmEnum;
import lombok.experimental.UtilityClass;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@UtilityClass
public class EncryptionUtils {

    public byte[] decodeBase64String(String value) {
        return Base64.getDecoder().decode(value);
    }

    public byte[] createHash(String valueForEncryption, String hashFunction)
            throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(hashFunction);
        return digest.digest(valueForEncryption.getBytes(StandardCharsets.UTF_8));
    }

    public String createHashInBase64String(String valueForEncryption, String hashFunction)
            throws NoSuchAlgorithmException {
        return encodeToBase64String(createHash(valueForEncryption, hashFunction));
    }

    public String encodeToBase64String(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public PublicKey createPublicKey(byte[] publicKey, String algorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(publicKey));
    }

    public PrivateKey createPrivateKey(String base64PrivateKey, String algorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedPrivateBytes = Base64.getDecoder().decode(base64PrivateKey);
        return KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(decodedPrivateBytes));
    }

    public KeyPair retrieveKeyPair(String algorithm) throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
        return kpg.generateKeyPair();
    }

    public byte[] calculateHmacSha256(byte[] secretKey, byte[] message) {
        byte[] hmacSha256;
        try {
            Mac mac = Mac.getInstance(AlgorithmEnum.HMACSHA256.getValue());
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, AlgorithmEnum.HMACSHA256.getValue());
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        return hmacSha256;
    }
}
