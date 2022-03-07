package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.entity.UserVerification;
import com.cleevio.vexl.module.user.enums.AlgorithmEnum;
import com.cleevio.vexl.module.user.exception.DigitalSignatureException;
import com.cleevio.vexl.module.user.exception.VerificationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
public class SignatureServiceTest {

    private SignatureService signatureService;

    @Mock
    User user;

    private final static String PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEzIdBL0Q/P+OEk84pJTaEIwro2mY9Y3JihBzNlMn5jTxVtzyi0MEepbgu57Z5nBZG6kNo0D8FTrY0Oe/2niL13w==";

    @BeforeEach
    public void setup() {
        this.signatureService = new SignatureService(PUBLIC_KEY);
    }

    @Test
    void verificationMissingTest() {
        assertThrows(VerificationNotFoundException.class, () -> signatureService.createSignature(user, AlgorithmEnum.EdDSA.getValue()));
    }
}
