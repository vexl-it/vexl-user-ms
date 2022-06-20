package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.integration.twilio.config.TwilioConfig;
import com.cleevio.vexl.module.sms.service.SmsService;
import com.cleevio.vexl.module.user.config.SecretKeyConfig;
import com.cleevio.vexl.module.user.dto.request.PhoneConfirmRequest;
import com.cleevio.vexl.module.user.entity.UserVerification;
import com.cleevio.vexl.module.user.exception.UserPhoneInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;


@IntegrationTest
public class UserVerificationServiceTest {

    @Mock
    private UserVerificationRepository verificationRepository;

    @Mock
    private UserVerification verification;

    private UserVerificationService verificationService;

    @Mock
    private SmsService smsService;

    @Mock
    private TwilioConfig twilioConfig;

    @Mock
    private ChallengeService challengeService;

    @Mock
    private UserService userService;

    private final static SecretKeyConfig secretKey =
            new SecretKeyConfig("dummy_public_key", "dummy_private_key", "dummy_hmac", "dummy_aes");

    private final int phoneDigitsLength = 6;

    private final int expirationTime = 30;

    private static final String PHONE = "+420752653958";

    @BeforeEach
    public void setup() {
        this.verificationService = new UserVerificationService(smsService, verificationRepository, challengeService, userService, twilioConfig, secretKey, phoneDigitsLength, expirationTime);
    }

    @Test
    void createTest() throws UserPhoneInvalidException {
        PhoneConfirmRequest phoneConfirmRequest = new PhoneConfirmRequest(PHONE);
        Mockito.when(this.verificationRepository.save(any())).thenReturn(verification);
        Mockito.when(this.twilioConfig.getPhone()).thenReturn("+421011561651");
        verificationService.requestConfirmPhone(phoneConfirmRequest);
        Mockito.verify(verificationRepository).save(any());
    }

}
