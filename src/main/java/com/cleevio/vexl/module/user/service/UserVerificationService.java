package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.sms.service.SmsService;
import com.cleevio.vexl.module.user.dto.request.CodeConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.PhoneConfirmRequest;
import com.cleevio.vexl.module.user.dto.response.CodeConfirmResponse;
import com.cleevio.vexl.module.user.entity.UserVerification;
import com.cleevio.vexl.utils.PhoneUtils;
import com.cleevio.vexl.utils.RandomSecurityUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
@Slf4j
@AllArgsConstructor
public class UserVerificationService {

    private final SmsService smsService;
    private final UserVerificationRepository userVerificationRepository;

    @Value("#{new Integer('${verification.phone.digits:Length}')}")
    private Integer phoneDigitsLength;

    @Value("#{new Integer('${verification.phone.expiration.time:ExpirationTime}')}")
    private Integer expirationTime;

    public UserVerification requestConfirmPhone(PhoneConfirmRequest phoneConfirmRequest) {
        final String codeToSend = RandomSecurityUtils.retrieveRandomDigits(this.phoneDigitsLength);

        UserVerification userVerification = UserVerification.builder()
                .verificationCode(codeToSend)
                .expirationAt(Instant.now().plusSeconds(this.expirationTime))
                .build();

        smsService.sendMessage(userVerification,
                PhoneUtils.trimAndDeleteSpacesFromPhoneNumber(phoneConfirmRequest.getPhoneNumber()));

        return this.userVerificationRepository.save(userVerification);
    }

    @Transactional
    public CodeConfirmResponse requestConfirmCode(CodeConfirmRequest codeConfirmRequest) {
        userVerificationRepository.deleteExpiredVerifications(Instant.now());

        UserVerification userVerification = userVerificationRepository.findValidUserVerificationByIdAndCode(
                codeConfirmRequest.getId(),
                codeConfirmRequest.getCode(),
                Instant.now()
        );

        if (userVerification == null) {
            return new CodeConfirmResponse(false, "The code is already expired.");
        }

        userVerificationRepository.deleteVerificationById(userVerification.getId());

        return new CodeConfirmResponse(true, "The code is correct.");
    }
}
