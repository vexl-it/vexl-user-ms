package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.module.user.config.SecretKeyConfig;
import com.cleevio.vexl.module.user.dto.response.SignatureResponse;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.VerificationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creating and verifying signatures.
 * <p>
 * If the user verifies his challenge, his public key is verified,
 * and we can generate a certificate for him - a signed combination of
 * his public key and a hash of his phone number or facebookId.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SignatureService {

    private final SecretKeyConfig secretKey;

    @Transactional(readOnly = true)
    public SignatureResponse createSignature(User user)
            throws VerificationNotFoundException {
        log.info("Creating digital signature for user {}",
                user.getId());

        if (user.getUserVerification() == null || user.getUserVerification().getPhoneNumber() == null) {
            log.error("Verification is missing. We need verification for phone number.");
            throw new VerificationNotFoundException();
        }

        return createSignature(
                user.getPublicKey(),
                user.getUserVerification().getPhoneNumber(),
                true
        );
    }

    public SignatureResponse createSignature(String publicKey, String hash, boolean alreadyHashed) {

        if (!alreadyHashed) {
            hash = CLibrary.CRYPTO_LIB.hmac_digest(
                    hash,
                    this.secretKey.hmacKey()
            );
        }

        String input = String.join("", publicKey, hash);
        String digitalSignature = CLibrary.CRYPTO_LIB.ecdsa_sign(
                this.secretKey.signaturePublicKey(),
                this.secretKey.signaturePrivateKey(),
                input,
                input.length());

        return new SignatureResponse(
                hash,
                digitalSignature,
                true
        );
    }

    public boolean isSignatureValid(String publicKey, String hash, String signature) {
        String input = String.join("", publicKey, hash);
        return CLibrary.CRYPTO_LIB.ecdsa_verify(this.secretKey.signaturePublicKey(), input, input.length(), signature);
    }

}
