package com.cleevio.vexl.common;

import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.entity.UserVerification;
import com.cleevio.vexl.module.user.service.SignatureService;
import com.cleevio.vexl.module.user.service.UserService;
import com.cleevio.vexl.module.user.service.UserVerificationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

public class BaseControllerTest {

    @Autowired
    protected MockMvc mvc;

    @MockBean
    protected UserService userService;

    @MockBean
    protected SignatureService signatureService;

    @MockBean
    protected UserVerificationService userVerificationService;

    @Mock
    protected User user;

    @Mock
    protected UserVerification userVerification;

    @BeforeEach
    @SneakyThrows
    public void setup() {
        Mockito.when(user.getId()).thenReturn(1L);
        Mockito.when(user.getUsername()).thenReturn("Cermak");

        Mockito.when(userVerification.getId()).thenReturn(1L);
        Mockito.when(userVerification.getVerificationCode()).thenReturn("456");
        Mockito.when(userVerification.getExpirationAt()).thenReturn(Instant.now());

        Mockito.when(userService.existsUserByUsername(any())).thenReturn(false);
        Mockito.when(userService.findByPublicKey(any())).thenReturn(Optional.of(user));

    }
}
