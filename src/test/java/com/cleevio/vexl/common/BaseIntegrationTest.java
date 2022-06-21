package com.cleevio.vexl.common;

import com.cleevio.vexl.Application;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.entity.UserVerification;
import com.cleevio.vexl.module.user.service.ChallengeService;
import com.cleevio.vexl.module.user.service.SignatureService;
import com.cleevio.vexl.module.user.service.UserService;
import com.cleevio.vexl.module.user.service.UserVerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@Transactional
@ContextConfiguration(classes = Application.class)
@TestPropertySource(locations = "/application-test.properties")
public abstract class BaseIntegrationTest {

    protected static final String PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEzIdBL0Q/P+OEk84pJTaEIwro2mY9Y3JihBzNlMn5jTxVtzyi0MEepbgu57Z5nBZG6kNo0D8FTrY0Oe/2niL13w==";
    protected static final String PHONE_HASH = "GCzF7P15aLtu+LG6itgRfRKpOO+KKrdKZAnPzmTl1Fs=";
    protected static final String SIGNATURE = "/ty+wIsnpJu5XAcqTYs9FspaJct6YipVpIMqZTrMOglkisoU5E9jy5OiTVG/Gg5jVy+zEyc9KTHwJmIBcwlvDQ==";
    protected static final String USERNAME = "Vitezlas";
    protected static final byte[] AVATAR = "image/png;base64,iVBORw0KGgo".getBytes(StandardCharsets.UTF_8);

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

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    @SneakyThrows
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();
        this.userService.save(getUser());

        Mockito.when(signatureService.isSignatureValid(any(String.class), any(), any())).thenReturn(true);
        Mockito.when(userService.findByPublicKey(any(String.class))).thenReturn(Optional.of(getUser()));
        Mockito.when(userService.findByBase64PublicKey(any(String.class))).thenReturn(Optional.of(getUser()));
        Mockito.when(userService.existsUserByUsername(USERNAME)).thenReturn(true);

    }

    protected UserVerification getVerification(User user) {
        return UserVerification.builder()
                .user(user)
                .id(1L)
                .verificationCode("123")
                .expirationAt(ZonedDateTime.now().plusSeconds(30000))
                .phoneNumber("+420852852825")
                .challenge("challenge")
                .phoneVerified(true)
                .build();
    }

    /**
     * Generate testing user
     *
     * @return User
     */
    protected User getUser() {
        return User.builder()
                .username(USERNAME)
                .publicKey("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEzIdBL0Q/P+OEk84pJTaEIwro2mY9Y3JihBzNlMn5jTxVtzyi0MEepbgu57Z5nBZG6kNo0D8FTrY0Oe/2niL13w==")
                .avatar("AVATAR")
                .build();
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
