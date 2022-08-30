package com.cleevio.vexl.module.user.dto.request;

import com.cleevio.vexl.module.file.dto.request.ImageRequest;
import com.cleevio.vexl.module.user.serializer.TrimStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;


public record UserUpdateRequest(

        @Nullable
        @Schema(description = "Username in String format")
        @JsonDeserialize(using = TrimStringDeserializer.class)
        String username,

        @Nullable
        @Schema(description = "Base64 encoded file data including header. i.e.: data:image/png;base64,iVBORw0KGgo")
        ImageRequest avatar

) {
}
