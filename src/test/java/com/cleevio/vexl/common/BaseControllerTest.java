package com.cleevio.vexl.common;

import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.entity.UserVerification;
import com.cleevio.vexl.module.user.service.ChallengeService;
import com.cleevio.vexl.module.user.service.SignatureService;
import com.cleevio.vexl.module.user.service.UserService;
import com.cleevio.vexl.module.user.service.UserVerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

public class BaseControllerTest {

    protected static final String PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEzIdBL0Q/P+OEk84pJTaEIwro2mY9Y3JihBzNlMn5jTxVtzyi0MEepbgu57Z5nBZG6kNo0D8FTrY0Oe/2niL13w==";
    protected static final String PHONE_HASH = "GCzF7P15aLtu+LG6itgRfRKpOO+KKrdKZAnPzmTl1Fs=";
    protected static final String SIGNATURE = "/ty+wIsnpJu5XAcqTYs9FspaJct6YipVpIMqZTrMOglkisoU5E9jy5OiTVG/Gg5jVy+zEyc9KTHwJmIBcwlvDQ==";


    @Autowired
    protected MockMvc mvc;

    @MockBean
    protected UserService userService;

    @MockBean
    protected SignatureService signatureService;

    @MockBean
    protected UserVerificationService userVerificationService;

    @MockBean
    protected ChallengeService challengeService;

    @Mock
    protected User user;

    @Mock
    protected UserVerification userVerification;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    @SneakyThrows
    public void setup() {
        Mockito.when(user.getId()).thenReturn(1L);
        Mockito.when(user.getUsername()).thenReturn("Cermak");
        Mockito.when(user.getPublicKey()).thenReturn("1d6s51asd65s1ad65a15sa".getBytes(StandardCharsets.UTF_8));
        Mockito.when(user.getAvatar()).thenReturn("AVATAR");

        Mockito.when(userVerification.getId()).thenReturn(1L);
        Mockito.when(userVerification.getVerificationCode()).thenReturn("456");
        Mockito.when(userVerification.getExpirationAt()).thenReturn(ZonedDateTime.now());

        Mockito.when(userService.existsUserByUsername(any())).thenReturn(false);
        Mockito.when(userService.findByPublicKey(any())).thenReturn(Optional.of(user));
        Mockito.when(userService.findByBase64PublicKey((any()))).thenReturn(Optional.of(user));

        Mockito.when(signatureService.isSignatureValid(any(String.class), any(), any(), any(), any())).thenReturn(true);

    }

    /**
     * Entity to json string body helper
     *
     * @param obj Entity
     * @return JSON string
     */
    protected String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
