package com.cleevio.vexl.util;

import com.cleevio.vexl.module.user.dto.request.PhoneConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.UserCreateRequest;
import com.cleevio.vexl.module.user.dto.request.UserUpdateRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateRequestUtilTest {

    public static UserCreateRequest createUserCreateRequest(String username) {
        return new UserCreateRequest(
                username,
                null
        );
    }

    public static UserUpdateRequest createUserUpdateRequest(String username) {
        return new UserUpdateRequest(
                username,
                null
        );
    }
}
