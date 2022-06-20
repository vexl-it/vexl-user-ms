package com.cleevio.vexl.module.user.integration;

import com.cleevio.vexl.common.BaseIntegrationTest;
import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.module.user.dto.request.ChallengeRequest;
import com.cleevio.vexl.module.user.dto.response.SignatureResponse;
import com.cleevio.vexl.module.user.entity.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class ConfirmChallengePostTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/v1/user/confirmation/challenge";

    @BeforeEach
    @SneakyThrows
    public void setup() {

        super.setup();
    }

    @Test
    public void confirmChallengeValidPostTest() throws Exception {
        ChallengeRequest challengeRequest = new ChallengeRequest(PUBLIC_KEY, "challengeSignature");
        SignatureResponse signatureResponse = new SignatureResponse("testHash", "testSignature", true);

        Mockito.when(this.challengeService.isSignedChallengeValid(any(User.class), any(String.class)))
                .thenReturn(true);
        Mockito.when(this.signatureService.createSignature(any(User.class)))
                .thenReturn(signatureResponse);

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(challengeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hash", notNullValue()))
                .andExpect(jsonPath("$.signature", notNullValue()))
                .andExpect(jsonPath("$.challengeVerified", equalTo(true)));
    }

    @Test
    public void confirmChallengeNotValidTest() throws Exception {
        ChallengeRequest challengeRequest = new ChallengeRequest(PUBLIC_KEY, "challengeSignature");
        SignatureResponse signatureResponse = new SignatureResponse("testHash", "testSignature", true);

        Mockito.when(this.challengeService.isSignedChallengeValid(any(User.class), any(String.class)))
                .thenReturn(false);
        Mockito.when(this.signatureService.createSignature(any(User.class)))
                .thenReturn(signatureResponse);

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(challengeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.challengeVerified", equalTo(false)));
    }
}
