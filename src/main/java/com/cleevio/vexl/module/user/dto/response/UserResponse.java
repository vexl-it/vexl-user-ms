package com.cleevio.vexl.module.user.dto.response;

import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.serializer.Base64Serializer;
import com.cleevio.vexl.utils.EncryptionUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
public class UserResponse {

    private Long userId;

    private String username;

    private String avatar;

    private String publicKey;

    public UserResponse(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.avatar = user.getAvatar();
        this.publicKey = EncryptionUtils.encodeToBase64String(user.getPublicKey());
    }
}
