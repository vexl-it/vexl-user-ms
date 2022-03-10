package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.module.sms.service.SmsService;
import com.cleevio.vexl.module.user.dto.request.CodeConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.PhoneConfirmRequest;
import com.cleevio.vexl.module.user.dto.response.ConfirmCodeResponse;
import com.cleevio.vexl.module.user.entity.UserVerification;
import com.cleevio.vexl.module.user.enums.AlgorithmEnum;
import com.cleevio.vexl.module.user.exception.UserAlreadyExistsException;
import com.cleevio.vexl.module.user.exception.VerificationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;


@IntegrationTest
public class UserVerificationServiceTest {

    @Mock
    private UserVerificationRepository verificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserVerification verification;

    private UserVerificationService verificationService;

    @Mock
    private SmsService smsService;

    @Mock
    private ChallengeService challengeService;

    @Mock
    private UserService userService;

    private final int phoneDigitsLength = 6;

    private final int expirationTime = 30;

    private static final String PHONE = "+420752653958";
    private final static String PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEzIdBL0Q/P+OEk84pJTaEIwro2mY9Y3JihBzNlMn5jTxVtzyi0MEepbgu57Z5nBZG6kNo0D8FTrY0Oe/2niL13w==";

    @Value("${hmac.secret.key}")
    private String secretKey;

    @BeforeEach
    public void setup() {
        this.verificationService = new UserVerificationService(smsService, verificationRepository, challengeService, userService, phoneDigitsLength, expirationTime, secretKey);
    }

    @Test
    void createTest() {
        PhoneConfirmRequest phoneConfirmRequest = new PhoneConfirmRequest();
        phoneConfirmRequest.setPhoneNumber(PHONE);
        Mockito.when(this.verificationRepository.save(any())).thenReturn(verification);
        verificationService.requestConfirmPhone(phoneConfirmRequest);
        Mockito.verify(verificationRepository).save(any());
    }

}
