package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.integration.twilio.config.TwilioConfig;
import com.cleevio.vexl.module.sms.service.SmsService;
import com.cleevio.vexl.module.user.config.SecretKeyConfig;
import com.cleevio.vexl.module.user.dto.request.CodeConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.PhoneConfirmRequest;
import com.cleevio.vexl.module.user.entity.UserVerification;
import com.cleevio.vexl.module.user.exception.ChallengeGenerationException;
import com.cleevio.vexl.module.user.exception.UserAlreadyExistsException;
import com.cleevio.vexl.module.user.exception.UserPhoneInvalidException;
import com.cleevio.vexl.module.user.exception.VerificationNotFoundException;
import com.cleevio.vexl.utils.PhoneUtils;
import com.cleevio.vexl.utils.RandomSecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;

/**
 * Service for processing of request to phone number verification. Phone must be valid according industry-standard
 * notation pattern specified by ITU-T E.123. This service is also responsible for processing code verification and
 * generating challenge for user.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserVerificationService {

    private final SmsService smsService;
    private final UserVerificationRepository userVerificationRepository;
    private final ChallengeService challengeService;
    private final UserService userService;
    private final TwilioConfig twilioConfig;
    private final SecretKeyConfig secretKey;

    @Value("#{new Integer('${verification.phone.digits:Length}')}")
    private final Integer codeDigitsLength;

    @Value("#{new Integer('${verification.phone.expiration.time:ExpirationTime}')}")
    private final Integer expirationTime;

    /**
     * Generate a random code for phone verification. Store the unencrypted code and the phone encrypted with HMAC-SHA256 in the database
     * and create an entry in the USER_VERIFICATION table.
     *
     * @param phoneConfirmRequest
     * @return
     */
    @Transactional(rollbackOn = Exception.class)
    public UserVerification requestConfirmPhone(PhoneConfirmRequest phoneConfirmRequest)
            throws UserPhoneInvalidException {

        final String codeToSend;
        if ("devel".equals(twilioConfig.getPhone())) {
            codeToSend = "111111";
        } else {
            codeToSend = RandomSecurityUtils.retrieveRandomDigits(this.codeDigitsLength);

            smsService.sendMessage(codeToSend,
                    PhoneUtils.trimAndDeleteSpacesFromPhoneNumber(phoneConfirmRequest.phoneNumber()));
        }

        log.info("Creating user verification for new request for phone number verification.");
        UserVerification userVerification =
                createUserVerification(
                        codeToSend,
                        CLibrary.CRYPTO_LIB.hmac_digest(
                                phoneConfirmRequest.phoneNumber(),
                                this.secretKey.hmacKey()
                        )
                );

        UserVerification savedVerification = this.userVerificationRepository.save(userVerification);

        log.info("Created verification and code sent for verification id {}",
                savedVerification.getId());

        return savedVerification;
    }

    private UserVerification createUserVerification(String codeToSend, String phoneNumber) {
        return UserVerification.builder()
                .verificationCode(codeToSend)
                .expirationAt(ZonedDateTime.now().plusSeconds(this.expirationTime))
                .phoneNumber(phoneNumber)
                .build();
    }

    /**
     * Check if there is a valid verification for the ID and code. If there is, we create a challenge that verifies
     * that the user is giving us a public key to which he owns a private key.
     * We store the public key and the challenge, thus creating the basis for the USER entity.
     *
     * @param codeConfirmRequest
     * @return
     * @throws UserAlreadyExistsException
     * @throws ChallengeGenerationException
     * @throws VerificationNotFoundException
     */
    @Transactional(rollbackOn = Exception.class)
    public UserVerification requestConfirmCodeAndGenerateCodeChallenge(CodeConfirmRequest codeConfirmRequest)
            throws UserAlreadyExistsException, ChallengeGenerationException, VerificationNotFoundException {

        UserVerification userVerification =
                this.userVerificationRepository.findValidUserVerificationByIdAndCode(
                        codeConfirmRequest.id(),
                        codeConfirmRequest.code(),
                        ZonedDateTime.now()
                ).orElseThrow(VerificationNotFoundException::new);

        log.info("Code is verified for verification: {}", userVerification.getId());

        try {
            String challenge = this.challengeService.generateChallenge();
            log.info("Challenge is created.");

            userVerification.setChallenge(challenge);
            userVerification.setPhoneVerified(true);
            userVerification.setUser(
                    this.userService.prepareUser(codeConfirmRequest.userPublicKey()));

            return this.userVerificationRepository.save(userVerification);
        } catch (NoSuchAlgorithmException exception) {
            log.error("Challenge generation failed.", exception);
            throw new ChallengeGenerationException();
        }
    }

    public void deleteExpiredVerifications() {
        this.userVerificationRepository.deleteExpiredVerifications(ZonedDateTime.now().plusSeconds(this.expirationTime));
    }
}
