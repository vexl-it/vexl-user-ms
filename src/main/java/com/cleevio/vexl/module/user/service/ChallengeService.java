package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.dto.request.ChallengeRequest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.enums.AlgorithmEnum;
import com.cleevio.vexl.module.user.exception.VerificationNotFoundException;
import com.cleevio.vexl.module.user.exception.DigitalSignatureException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@Slf4j
@AllArgsConstructor
public class ChallengeService {


    private final SignatureService signatureService;

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

    public boolean isSignedChallengeValid(User user, ChallengeRequest challengeRequest)
            throws DigitalSignatureException, VerificationNotFoundException {

        if (user.getUserVerification() == null || user.getUserVerification().getChallenge() == null) {
            throw new VerificationNotFoundException();
        }

        return this.signatureService.isSignatureValid(
                Base64.getUrlDecoder().decode(user.getUserVerification().getChallenge()),
                challengeRequest.getSignature(),
                user.getPublicKey(),
                AlgorithmEnum.ECDSA.getValue(),
                AlgorithmEnum.ECIES.getValue()
        );
    }
}