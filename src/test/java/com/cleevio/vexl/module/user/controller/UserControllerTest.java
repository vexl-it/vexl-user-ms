package com.cleevio.vexl.module.user.controller;

import com.cleevio.vexl.common.BaseControllerTest;
import com.cleevio.vexl.common.exception.ApiException;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.user.dto.request.ChallengeRequest;
import com.cleevio.vexl.module.user.dto.request.CodeConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.PhoneConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.UserCreateRequest;
import com.cleevio.vexl.module.user.dto.request.UserUpdateRequest;
import com.cleevio.vexl.module.user.dto.request.UsernameAvailableRequest;
import com.cleevio.vexl.module.user.dto.response.SignatureResponse;
import com.cleevio.vexl.module.user.exception.UserErrorType;
import com.cleevio.vexl.module.user.exception.UsernameNotAvailable;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest extends BaseControllerTest {

    private static final String DEFAULT_EP = "/api/v1/user";
    private static final String ME_EP = DEFAULT_EP + "/me";
    private static final String REQUEST_SMS_EP = DEFAULT_EP + "/confirmation/phone";
    private static final String CONFIRM_SMS_AND_GENERATE_CHALLENGE = DEFAULT_EP + "/confirmation/code";
    private static final String CONFIRM_CHALLENGE = DEFAULT_EP + "/confirmation/challenge";
    private static final String FACEBOOK_ID = "dummy_facebook_id";
    private static final String FB_SIGNATURE_EP = DEFAULT_EP + "/signature/" + FACEBOOK_ID;
    private static final String USERNAME_AVAILABLE_EP = DEFAULT_EP + "/username/availability";
    private static final PhoneConfirmRequest PHONE_CONFIRM_REQUEST;
    private static final CodeConfirmRequest CODE_CONFIRM_REQUEST;
    private static final ChallengeRequest CHALLENGE_REQUEST;
    private static final SignatureResponse SIGNATURE_RESPONSE;
    private static final UsernameAvailableRequest USERNAME_AVAILABLE_REQUEST;
    private static final UserCreateRequest USER_CREATE_REQUEST;
    private static final UserUpdateRequest USER_UPDATE_REQUEST;
    private static final String USER_PHONE = "+420856856856";


    private static final String INVALID_PHONE_REQUEST = """
            {
                "phoneNumber": "INVALID_PHONE_FORMAT"
            }
                                                """;

    private static final String INVALID_REGISTER_USER_REQUEST = """
            {
                "username": "",
            }
                                                            """;

    static {
        PHONE_CONFIRM_REQUEST = new PhoneConfirmRequest(USER_PHONE);

        CODE_CONFIRM_REQUEST = new CodeConfirmRequest(1L, VERIFICATION_CODE, USER_PUBLIC_KEY);

        CHALLENGE_REQUEST = new ChallengeRequest(USER_PUBLIC_KEY, SIGNATURE_CHALLENGE);

        SIGNATURE_RESPONSE = new SignatureResponse(PHONE_HASH, SIGNATURE, true);

        USERNAME_AVAILABLE_REQUEST = new UsernameAvailableRequest(USER_NAME);

        USER_CREATE_REQUEST = new UserCreateRequest(USER_NAME, null);

        USER_UPDATE_REQUEST = new UserUpdateRequest(USER_NAME, null);
    }

    @Test
    void testRequestConfirmPhone_validInput_shouldReturn200() throws Exception {
        when(userVerificationService.requestConfirmPhone(PHONE_CONFIRM_REQUEST)).thenReturn(USER_VERIFICATION);

        mvc.perform(post(REQUEST_SMS_EP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(PHONE_CONFIRM_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationId", is(USER_VERIFICATION.getId())));
    }

    @Test
    void testRequestConfirmPhone_invalidInput_shouldReturn400() throws Exception {
        mvc.perform(post(REQUEST_SMS_EP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_PHONE_REQUEST))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testConfirmCodeAndGenerateCodeChallenge_validInput_shouldReturn200() throws Exception {
        when(userVerificationService.requestConfirmCodeAndGenerateCodeChallenge(CODE_CONFIRM_REQUEST)).thenReturn(USER_VERIFICATION);

        mvc.perform(post(CONFIRM_SMS_AND_GENERATE_CHALLENGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(CODE_CONFIRM_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.challenge", is(USER_VERIFICATION.getChallenge())))
                .andExpect(jsonPath("$.phoneVerified", is(USER_VERIFICATION.isPhoneVerified())));
    }

    @Test
    void testVerifyChallengeAndGenerateSignature_validInput_shouldReturn200() throws Exception {
        when(challengeService.isSignedChallengeValid(USER, CHALLENGE_REQUEST.signature())).thenReturn(true);
        when(signatureService.createSignature(USER)).thenReturn(SIGNATURE_RESPONSE);

        mvc.perform(post(CONFIRM_CHALLENGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(CHALLENGE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hash", is(PHONE_HASH)))
                .andExpect(jsonPath("$.signature", is(SIGNATURE)))
                .andExpect(jsonPath("$.challengeVerified", equalTo(true)));
    }

    @Test
    void testVerifyChallengeAndGenerateSignature_validInput_invalidChallenge_shouldReturn200() throws Exception {
        when(userService.findByPublicKey(any())).thenReturn(Optional.of(USER));
        when(challengeService.isSignedChallengeValid(USER, CHALLENGE_REQUEST.signature())).thenReturn(false);

        mvc.perform(post(CONFIRM_CHALLENGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(CHALLENGE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hash", nullValue()))
                .andExpect(jsonPath("$.signature", nullValue()))
                .andExpect(jsonPath("$.challengeVerified", equalTo(false)));
    }

    @Test
    void testGenerateSignature_validInput_shouldReturn200() throws Exception {
        when(signatureService.createSignature(USER.getPublicKey(), FACEBOOK_ID, false)).thenReturn(SIGNATURE_RESPONSE);

        mvc.perform(get(FB_SIGNATURE_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hash", is(PHONE_HASH)))
                .andExpect(jsonPath("$.signature", is(SIGNATURE)))
                .andExpect(jsonPath("$.challengeVerified", equalTo(true)));
    }

    @Test
    void testUsernameAvailable_validInput_shouldReturn200() throws Exception {
        when(userService.existsUserByUsername(USERNAME_AVAILABLE_REQUEST.username())).thenReturn(false);

        mvc.perform(post(USERNAME_AVAILABLE_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(USERNAME_AVAILABLE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable", equalTo(true)));
    }

    @Test
    public void testRegisterNewUser_validInput_shouldReturn200() throws Exception {
        when(userService.create(USER, USER_CREATE_REQUEST)).thenReturn(USER);
        mvc.perform(post(DEFAULT_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(USER_CREATE_REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is(USER.getUsername())))
                .andExpect(jsonPath("$.avatar", is(USER.getAvatar())))
                .andExpect(jsonPath("$.publicKey", is(USER.getPublicKey())));
    }


    @Test
    public void testRegisterUserWithExistingUsername_invalidInput_shouldReturn409() throws Exception {
        when(userService.create(USER, USER_CREATE_REQUEST)).thenThrow(UsernameNotAvailable.class);

        mvc.perform(post(DEFAULT_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(USER_CREATE_REQUEST)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", Matchers.is(ApiException.Module.USER.getErrorCode() + UserErrorType.USERNAME_NOT_AVAILABLE.getCode())))
                .andExpect(jsonPath("$.message[0]", Matchers.is(UserErrorType.USERNAME_NOT_AVAILABLE.getMessage())));
    }

    @Test
    public void testRegisterUserWithoutUsername_invalidInput_shouldReturn400() throws Exception {
        mvc.perform(post(DEFAULT_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_REGISTER_USER_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", Matchers.is("0")));
    }

    @Test
    public void testUpdateMe_validInput_shouldReturn200() throws Exception {
        when(userService.update(USER, USER_UPDATE_REQUEST)).thenReturn(USER);

        mvc.perform(put(ME_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(USER_UPDATE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(USER.getUsername())))
                .andExpect(jsonPath("$.avatar", is(USER.getAvatar())))
                .andExpect(jsonPath("$.publicKey", is(USER.getPublicKey())));
    }

    @Test
    public void testUpdateMe_invalidInput_duplicateUsername_shouldReturn409() throws Exception {
        when(userService.update(USER, USER_UPDATE_REQUEST)).thenThrow(UsernameNotAvailable.class);

        mvc.perform(put(ME_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(USER_UPDATE_REQUEST)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", Matchers.is(ApiException.Module.USER.getErrorCode() + UserErrorType.USERNAME_NOT_AVAILABLE.getCode())))
                .andExpect(jsonPath("$.message[0]", Matchers.is(UserErrorType.USERNAME_NOT_AVAILABLE.getMessage())));
    }

    @Test
    public void testGetMe_validInput_shouldReturn200() throws Exception {
        mvc.perform(get(ME_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(USER.getUsername())))
                .andExpect(jsonPath("$.avatar", is(USER.getAvatar())))
                .andExpect(jsonPath("$.publicKey", is(USER.getPublicKey())));
    }

    @Test
    public void testDeleteMe_validInput_shouldReturn200() throws Exception {
        mvc.perform(delete(ME_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}