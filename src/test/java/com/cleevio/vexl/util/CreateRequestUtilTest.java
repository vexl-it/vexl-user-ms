package com.cleevio.vexl.util;

import com.cleevio.vexl.module.file.dto.request.ImageRequest;
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

    public static UserCreateRequest createUserCreateRequestWithAvatar(String username) {
        return new UserCreateRequest(
                username,
                new ImageRequest("png", "YXNkYXNkc2FkYXNkYXM=")
        );
    }

    public static UserUpdateRequest createUserUpdateRequest(String username) {
        return new UserUpdateRequest(
                username,
                null
        );
    }
}
