package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.module.user.config.SecretKeyConfig;
import com.cleevio.vexl.module.user.dto.SignatureData;
import com.cleevio.vexl.module.user.dto.UserData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

import static com.cleevio.vexl.module.user.util.ChallengeUtil.isSignedChallengeValid;

/**
 * Creating and verifying signatures.
 * <p>
 * If the user verifies his challenge, his public key is verified,
 * and we can generate a certificate for him - a signed combination of
 * his public key and a hash of his phone number or facebookId.
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class SignatureService {

    private final SecretKeyConfig secretKey;

    @Transactional(readOnly = true)
    public SignatureData createSignature(@Valid final UserData userData, final int cryptoVersion) {
        if (!isSignedChallengeValid(userData, cryptoVersion)) {
            return new SignatureData(null, null, false);
        }

        return createSignature(
                userData.publicKey(),
                userData.phoneNumber(),
                true,
                cryptoVersion
        );
    }

    public SignatureData createSignature(String publicKey, String hash, boolean alreadyHashed, final int cryptoVersion) {

        if (!alreadyHashed) {
            hash = CLibrary.CRYPTO_LIB.hmac_digest(
                    this.secretKey.hmacKey(),
                    hash
            );
        }

        final String input = String.join(StringUtils.EMPTY, publicKey, hash);
        final String digitalSignature = cryptoVersion >= 2 ? CLibrary.CRYPTO_LIB.ecdsa_sign_v2(
                this.secretKey.signaturePublicKey(),
                this.secretKey.signaturePrivateKey(),
                input,
                input.length()) : CLibrary.CRYPTO_LIB.ecdsa_sign(
                this.secretKey.signaturePublicKey(),
                this.secretKey.signaturePrivateKey(),
                input,
                input.length());

        return new SignatureData(
                hash,
                digitalSignature,
                true
        );
    }

    public boolean isSignatureValid(String publicKey, String hash, String signature, final int cryptoVersion) {
        final String input = String.join(StringUtils.EMPTY, publicKey, hash);

        if (cryptoVersion >= 2) {
            return CLibrary.CRYPTO_LIB.ecdsa_verify_v2(this.secretKey.signaturePublicKey(), input, input.length(), signature);
        }
        return CLibrary.CRYPTO_LIB.ecdsa_verify(this.secretKey.signaturePublicKey(), input, input.length(), signature);
    }

}
