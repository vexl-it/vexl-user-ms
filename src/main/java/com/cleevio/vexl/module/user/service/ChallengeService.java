package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.VerificationNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service for generating challenge and processing of verification of challenge.
 * <p>
 * Challenge is an important element in verifying that a user has a private key to his public key.
 * We create a random challenge for him, he has to sign it with his private key, and then we verify with his public key that it is indeed signed by him.
 */
@Service
@Slf4j
@AllArgsConstructor
public class ChallengeService {

    public String generateChallenge()
            throws NoSuchAlgorithmException {
        byte[] bytes = generateCodeVerifier().getBytes(StandardCharsets.US_ASCII);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(bytes, 0, bytes.length);
        byte[] digest = messageDigest.digest();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

    private String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

    public boolean isSignedChallengeValid(User user, String signature)
            throws VerificationNotFoundException {

        if (user.getUserVerification() == null || user.getUserVerification().getChallenge() == null) {
            throw new VerificationNotFoundException();
        }

        return CLibrary.CRYPTO_LIB.ecdsa_verify(
                user.getPublicKey(),
                user.getUserVerification().getChallenge(),
                user.getUserVerification().getChallenge().length(),
                signature
        );
    }
}
