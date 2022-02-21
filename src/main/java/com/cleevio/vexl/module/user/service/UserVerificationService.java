package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.sms.service.SmsService;
import com.cleevio.vexl.module.user.dto.request.CodeConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.PhoneConfirmRequest;
import com.cleevio.vexl.module.user.dto.response.ConfirmCodeResponse;
import com.cleevio.vexl.module.user.entity.UserVerification;
import com.cleevio.vexl.module.user.exception.UserAlreadyExistsException;
import com.cleevio.vexl.utils.EncryptionUtils;
import com.cleevio.vexl.utils.PhoneUtils;
import com.cleevio.vexl.utils.RandomSecurityUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Service
@Slf4j
@AllArgsConstructor
public class UserVerificationService {

    private final SmsService smsService;
    private final UserVerificationRepository userVerificationRepository;
    private final ChallengeService challengeService;
    private final UserService userService;

    @Value("#{new Integer('${verification.phone.digits:Length}')}")
    private Integer phoneDigitsLength;

    @Value("#{new Integer('${verification.phone.expiration.time:ExpirationTime}')}")
    private Integer expirationTime;

    public UserVerification requestConfirmPhone(PhoneConfirmRequest phoneConfirmRequest, byte[] phoneAlgorithmSecret) {
        final String codeToSend = RandomSecurityUtils.retrieveRandomDigits(this.phoneDigitsLength);

        UserVerification userVerification =
                createUserVerification(
                        codeToSend,
                        EncryptionUtils.calculateHmacSha256(
                                phoneAlgorithmSecret,
                                phoneConfirmRequest.getPhoneNumber().getBytes(StandardCharsets.UTF_8)
                        )
                );

        smsService.sendMessage(userVerification,
                PhoneUtils.trimAndDeleteSpacesFromPhoneNumber(phoneConfirmRequest.getPhoneNumber()));

        return this.userVerificationRepository.save(userVerification);
    }

    private UserVerification createUserVerification(String codeToSend, byte[] phoneNumber) {
        return UserVerification.builder()
                .verificationCode(codeToSend)
                .expirationAt(Instant.now().plusSeconds(this.expirationTime))
                .phoneNumber(phoneNumber)
                .build();
    }

    @Transactional
    public ConfirmCodeResponse requestConfirmCodeAndGenerateCodeChallenge(CodeConfirmRequest codeConfirmRequest)
            throws NoSuchAlgorithmException, UserAlreadyExistsException {

        UserVerification userVerification = this.userVerificationRepository.findValidUserVerificationByIdAndCode(
                codeConfirmRequest.getId(),
                codeConfirmRequest.getCode(),
                Instant.now()
        );

        if (userVerification == null) {
            return new ConfirmCodeResponse(userVerification);
        }

        String challenge = this.challengeService.generateChallenge();

        userVerification.setChallenge(challenge);
        userVerification.setPhoneVerified(true);
        userVerification.setUser(
                this.userService.prepareUser(codeConfirmRequest.getUserPublicKey())
        );

        return new ConfirmCodeResponse(this.userVerificationRepository.save(userVerification));
    }

    public void deleteExpiredVerifications() {
        this.userVerificationRepository.deleteExpiredVerifications(Instant.now().plusSeconds(this.expirationTime));
    }
}
