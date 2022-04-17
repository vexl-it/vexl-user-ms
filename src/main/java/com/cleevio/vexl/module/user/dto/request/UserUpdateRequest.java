package com.cleevio.vexl.module.user.dto.request;

import com.cleevio.vexl.module.file.dto.request.ImageRequest;
import com.cleevio.vexl.module.user.serializer.Base64Deserializer;
import com.cleevio.vexl.module.user.serializer.TrimStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @Schema(required = true, description = "Username in String format")
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private final String username;

    @Schema(description = "Base64 encoded file data including header. i.e.: data:image/png;base64,iVBORw0KGgo")
    @JsonDeserialize(using = Base64Deserializer.class)
    private final ImageRequest avatar;

    public static UserCreateRequest of(String username, byte[] avatar) {
        return new UserCreateRequest(
                username,
                new ImageRequest());
    }
}
