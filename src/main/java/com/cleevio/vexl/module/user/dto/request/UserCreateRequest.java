package com.cleevio.vexl.module.user.dto.request;

import com.cleevio.vexl.module.user.serializer.Base64Deserializer;
import com.cleevio.vexl.module.user.serializer.TrimStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserCreateRequest {

    @NotBlank
    @Schema(required = true, description = "Username in String format.")
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private final String username;

    @Schema(description = "Base64 encoded file data.")
    @JsonDeserialize(using = Base64Deserializer.class)
    private final byte[] avatar;

    public static UserCreateRequest of(String username, byte[] avatar) {
        return new UserCreateRequest(
                username,
                avatar);
    }

}
