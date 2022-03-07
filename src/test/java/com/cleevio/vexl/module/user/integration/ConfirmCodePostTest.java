package com.cleevio.vexl.module.user.integration;

import com.cleevio.vexl.common.BaseIntegrationTest;
import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.module.user.dto.request.CodeConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.PhoneConfirmRequest;
import com.cleevio.vexl.module.user.dto.response.ConfirmCodeResponse;
import com.cleevio.vexl.module.user.entity.UserVerification;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.Random;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class ConfirmCodePostTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/v1/user/confirmation/code";

    @BeforeEach
    @SneakyThrows
    public void setup() {
        super.setup();
    }

    @Test
    public void confirmCodeValidPostTest() throws Exception {

        Mockito.when(userVerificationService.requestConfirmCodeAndGenerateCodeChallenge(any(CodeConfirmRequest.class)))
                .thenReturn(new ConfirmCodeResponse(this.getVerification(this.getUser())));

        CodeConfirmRequest codeConfirmRequest = new CodeConfirmRequest();
        codeConfirmRequest.setId(1L);
        codeConfirmRequest.setCode("123987");
        codeConfirmRequest.setUserPublicKey(PUBLIC_KEY);

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(codeConfirmRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.challenge", notNullValue()))
                .andExpect(jsonPath("$.phoneVerified", equalTo(true)));
    }

    @Test
    public void confirmCodeInvalidPostTest() throws Exception {

        UserVerification userVerification = new UserVerification();
        userVerification.setVerificationCode(null);
        userVerification.setPhoneVerified(false);

        ConfirmCodeResponse confirmCodeResponse = new ConfirmCodeResponse(userVerification);

        Mockito.when(userVerificationService.requestConfirmCodeAndGenerateCodeChallenge(any(CodeConfirmRequest.class)))
                .thenReturn(confirmCodeResponse);

        CodeConfirmRequest codeConfirmRequest = new CodeConfirmRequest();
        codeConfirmRequest.setId(1L);
        codeConfirmRequest.setCode("123987");
        codeConfirmRequest.setUserPublicKey(PUBLIC_KEY);

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(codeConfirmRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneVerified", equalTo(false)));
    }

}
