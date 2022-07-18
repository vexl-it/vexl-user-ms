package com.cleevio.vexl.module.user.dto.request;

import com.cleevio.vexl.module.file.dto.request.ImageRequest;
import com.cleevio.vexl.module.user.annotation.ValidAvatar;
import com.cleevio.vexl.module.user.serializer.TrimStringDeserializer;
import com.cleevio.vexl.module.user.validator.AvatarConstraints;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ValidAvatar
public class UserCreateRequest implements AvatarConstraints {

    @NotBlank
    @Schema(required = true, description = "Username in String format.")
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private final String username;

    private final ImageRequest avatar;

}
