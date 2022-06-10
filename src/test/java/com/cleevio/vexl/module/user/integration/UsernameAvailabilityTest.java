package com.cleevio.vexl.module.user.integration;

import com.cleevio.vexl.common.BaseIntegrationTest;
import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.user.dto.request.UsernameAvailableRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class UsernameAvailabilityTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/v1/user/username/availability";

    @BeforeEach
    @SneakyThrows
    public void setup() {
        super.setup();
    }

    @Test
    public void nicknameAvailabilityValidTest() throws Exception {

        UsernameAvailableRequest request = new UsernameAvailableRequest();
        request.setUsername("Vladimir518");

        mvc.perform(post(BASE_URL)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable", equalTo(true)));
    }

    @Test
    public void nicknameNotAvailableTest() throws Exception {

        UsernameAvailableRequest request = new UsernameAvailableRequest();
        request.setUsername(USERNAME);

        mvc.perform(post(BASE_URL)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable", equalTo(false)));
    }


}
