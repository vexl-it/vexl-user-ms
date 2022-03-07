package com.cleevio.vexl.module.user.integration;

import com.cleevio.vexl.common.BaseIntegrationTest;
import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.user.dto.request.PhoneConfirmRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;


import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class ConfirmPhonePostTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/v1/user/confirmation/phone";

    @BeforeEach
    @SneakyThrows
    public void setup() {
        super.setup();
    }

    @Test
    public void postTest() throws Exception {

        Mockito.when(userVerificationService.requestConfirmPhone(any(PhoneConfirmRequest.class), any(byte[].class)))
                .thenReturn(this.getVerification(this.getUser()));

        PhoneConfirmRequest phoneConfirmRequest = new PhoneConfirmRequest();
        phoneConfirmRequest.setPhoneNumber("+420731958659");

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(phoneConfirmRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationId", notNullValue()))
                .andExpect(jsonPath("$.expirationAt", notNullValue()));
    }

}
