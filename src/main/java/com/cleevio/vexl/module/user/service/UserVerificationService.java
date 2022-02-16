package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.sms.service.SmsService;
import com.cleevio.vexl.module.user.dto.request.CodeConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.PhoneConfirmRequest;
import com.cleevio.vexl.module.user.dto.response.ConfirmCodeResponse;
import com.cleevio.vexl.module.user.entity.UserVerification;
import com.cleevio.vexl.module.user.exception.UserCreationException;
import com.cleevio.vexl.utils.PhoneUtils;
import com.cleevio.vexl.utils.RandomSecurityUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;

@Service
@Slf4j
@AllArgsConstructor
public class UserVerificationService {

    private final SmsService smsService;
    private final UserVerificationRepository userVerificationRepository;
    private final SignatureService signatureService;
    private final UserService userService;

    @Value("#{new Integer('${verification.phone.digits:Length}')}")
    private Integer phoneDigitsLength;

    @Value("#{new Integer('${verification.phone.expiration.time:ExpirationTime}')}")
    private Integer expirationTime;

    public UserVerification requestConfirmPhone(PhoneConfirmRequest phoneConfirmRequest) {
        final String codeToSend = RandomSecurityUtils.retrieveRandomDigits(this.phoneDigitsLength);

        UserVerification userVerification = UserVerification.builder()
                .verificationCode(codeToSend)
                .expirationAt(Instant.now().plusSeconds(this.expirationTime))
                .phoneNumber(phoneConfirmRequest.getPhoneNumber())
                .build();

        smsService.sendMessage(userVerification,
                PhoneUtils.trimAndDeleteSpacesFromPhoneNumber(phoneConfirmRequest.getPhoneNumber()));

        return this.userVerificationRepository.save(userVerification);
    }

    @Transactional
    public ConfirmCodeResponse requestConfirmCodeAndGenerateCert(CodeConfirmRequest codeConfirmRequest)
            throws NoSuchAlgorithmException, IOException, SignatureException, InvalidKeySpecException, InvalidKeyException, UserCreationException {

        UserVerification userVerification = this.userVerificationRepository.findValidUserVerificationByIdAndCode(
                codeConfirmRequest.getId(),
                codeConfirmRequest.getCode(),
                Instant.now()
        );

        if (userVerification == null) {
            return ConfirmCodeResponse
                    .builder()
                    .valid(false)
                    .build();
        }

        ConfirmCodeResponse confirmCodeResponse = this.signatureService.createSignature(codeConfirmRequest, userVerification.getPhoneNumber());
        this.userService.prepareUserWithPublicKey(confirmCodeResponse.getPublicKey());
        this.userVerificationRepository.deleteVerificationById(userVerification.getId());

        return confirmCodeResponse;
    }
}
