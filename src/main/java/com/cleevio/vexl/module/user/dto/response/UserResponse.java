package com.cleevio.vexl.module.user.dto.response;

import com.cleevio.vexl.module.user.entity.User;

public record UserResponse(

        Long userId,

        String username,

        String avatar,

        String publicKey

) {

    public UserResponse(User user) {
        this(
                user.getId(),
                user.getUsername(),
                user.getAvatar(),
                user.getPublicKey()
        );
    }
}
