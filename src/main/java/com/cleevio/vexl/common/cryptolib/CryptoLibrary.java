package com.cleevio.vexl.common.cryptolib;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class CryptoLibrary {
    private static CryptoLibrary instance;

    public static CryptoLibrary getInstance() {
        try {
            if (instance == null) {
                instance = new CryptoLibrary();
            }
            return instance;
        } catch(Exception e) {
            throw new RuntimeException("Error getting crypto library instance", e);
        }
    }

    private final KeyFactory keyFactory;

    public CryptoLibrary() throws NoSuchAlgorithmException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        keyFactory = KeyFactory.getInstance("EC", "BC");
    }

    private X509EncodedKeySpec base64ToKeySpec(String base64Key) {
        byte[] keyBytes = Base64
                .getDecoder()
                .decode(
                        new String(Base64.getDecoder().decode(base64Key))
                                .replaceAll("-----(BEGIN|END).*", "")
                                .replaceAll("\n", "")
                );

        return new X509EncodedKeySpec(keyBytes);
    }

    private PublicKey base64ToPublicKey(String base64Key) throws InvalidKeySpecException {
        return keyFactory.generatePublic(base64ToKeySpec(base64Key));
    }

    private PrivateKey base64ToPrivateKey(String base64Key) throws InvalidKeySpecException {
        byte[] keyBytes = Base64
                .getDecoder()
                .decode(
                        new String(Base64.getDecoder().decode(base64Key))
                                .replaceAll("-----(BEGIN|END).*", "")
                                .replaceAll("\n", "")
                );

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return keyFactory.generatePrivate(spec);
    }

    public boolean ecdsaVerifyV2(String base64PublicKey, String data, String base64Signature) {
        try {
            PublicKey pubKey = base64ToPublicKey(base64PublicKey);
            Signature ecdsaSign = Signature
                    .getInstance("SHA256withECDSA", "BC");
            ecdsaSign.initVerify(pubKey);
            ecdsaSign.update(data.getBytes(StandardCharsets.UTF_8));
            return ecdsaSign.verify(Base64.getDecoder().decode(base64Signature));
        } catch (Exception e) {
            // Todo log
            return false;
        }
    }

    public String ecdsaSignV2(String base64PrivateKey, String data) {
        try {
            PrivateKey privateKey = base64ToPrivateKey(base64PrivateKey);
            Signature ecdsaSign = Signature
                    .getInstance("SHA256withECDSA", "BC");
            ecdsaSign.initSign(privateKey);
            ecdsaSign.update(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(ecdsaSign.sign());
        } catch (Exception e) {
            // TODO handle
            return null;
        }
    }

    public boolean ecdsaVerifyV1(String base64PublicKey, String data, String base64Signature) {
        try {
            PublicKey pubKey = base64ToPublicKey(base64PublicKey);
            Signature ecdsaSign = Signature
                    .getInstance("NoneWithECDSA", "BC");
            ecdsaSign.initVerify(pubKey);

            byte[] dataHash = sha256(data).getBytes(StandardCharsets.UTF_8);

            ecdsaSign.update(dataHash);
            return ecdsaSign.verify(Base64.getDecoder().decode(base64Signature));
        } catch (Exception e) {
            // TODO handle
            return false;
        }
    }

    public String ecdsaSignV1(String base64PrivateKey, String data) {
        try {
            PrivateKey privKey = base64ToPrivateKey(base64PrivateKey);
            Signature ecdsaSign = Signature
                    .getInstance("NoneWithECDSA", "BC");
            ecdsaSign.initSign(privKey);

            ecdsaSign.update(sha256(data).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(ecdsaSign.sign());
        } catch (Exception e) {
            // TODO handle
            return  null;
        }
    }

    public String sha256(String digest) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = md.digest(digest.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(bytes);
    }

    public String hmacDigest(String password, String message){
        try {
            byte[] stretched = pbkd2f(password, "PBKDF2WithHmacSHA256", 108);
            byte[] sub = Arrays.copyOfRange(stretched, 44, 108);

            HMac hmac = new HMac(new SHA256Digest());
            hmac.init(new KeyParameter(sub));

            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            byte[] result = new byte[hmac.getMacSize()];
            hmac.update(messageBytes, 0, messageBytes.length);
            hmac.doFinal(result, 0);

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            // TODO handle
            return null;
        }
    }

    public boolean hmacVerify(String password, String message, String signature) throws Exception {
        String hmac = hmacDigest(password, message);
        return hmac.equals(signature);
    }

    public String aesEncrypt(String password, String message) {
        // TODO version string?
        try {
            byte[] stretchedPass = pbkd2f(password, "PBKDF2WithHmacSHA1", 32 + 12);

            byte[] cipherKey = Arrays.copyOfRange(stretchedPass, 0, 32);
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2};
            System.arraycopy(stretchedPass, 32, iv, 0, 12);

            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(cipherKey, "AES"), new IvParameterSpec(iv), new SecureRandom());
            var encrypted = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            // TODO
            return null;
        }
    }

    public String aesDecrypt(String password, String encrypted) {
        try {
            byte[] stretchedPass = pbkd2f(password, "PBKDF2WithHmacSHA1", 32 + 12);

            byte[] cipherKey = Arrays.copyOfRange(stretchedPass, 0, 32);
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2};
            System.arraycopy(stretchedPass, 32, iv, 0, 12);

            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(cipherKey, "AES"), new IvParameterSpec(iv), new SecureRandom());
            return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param data
     * @param alg PBKDF2WithHmacSHA256 or PBKDF2WithHmacSHA1, etc
     * @return
     */
    private byte[] pbkd2f(String data, String alg, int lengthBytes) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(data.toCharArray(), "vexlvexl".getBytes(StandardCharsets.UTF_8), 2000, lengthBytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(alg, "BC");

        return skf.generateSecret(spec).getEncoded();
    }
}
