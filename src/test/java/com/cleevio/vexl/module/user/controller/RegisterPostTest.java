package com.cleevio.vexl.module.user.controller;

import com.cleevio.vexl.common.BaseControllerTest;
import com.cleevio.vexl.common.exception.ApiException;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.user.dto.request.UserCreateRequest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.UserErrorType;
import com.cleevio.vexl.module.user.exception.UsernameNotAvailable;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class RegisterPostTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/user";

    @BeforeEach
    @SneakyThrows
    public void setup() {
        super.setup();

        Mockito.when(userService.create(any(User.class), any(UserCreateRequest.class))).thenReturn(user);
    }

    @Test
    public void registerNewUser() throws Exception {
        mvc.perform(post(BASE_URL)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new UserCreateRequest("David", "image/png;base64,iVBORw0KGgo"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.username", notNullValue()))
                .andExpect(jsonPath("$.avatar", notNullValue()))
                .andExpect(jsonPath("$.publicKey", notNullValue()));
    }


    @Test
    public void registerUserWithExistingUsername() throws Exception {
        Mockito.when(userService.create(any(User.class), any(UserCreateRequest.class))).thenThrow(UsernameNotAvailable.class);

        mvc.perform(post(BASE_URL)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new UserCreateRequest("Bartolomej", "image/png;base64,iVBORw0KGgo"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is(ApiException.Module.USER.getErrorCode() + UserErrorType.USERNAME_NOT_AVAILABLE.getCode())))
                .andExpect(jsonPath("$.message[0]", is(UserErrorType.USERNAME_NOT_AVAILABLE.getMessage())));
    }

    @Test
    public void registerUserWithoutUsername() throws Exception {

        mvc.perform(post(BASE_URL)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new UserCreateRequest(null, "image/png;base64,iVBORw0KGgo"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("0")));
    }
}
