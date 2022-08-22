package com.cleevio.vexl.module.user.config;

import com.cleevio.vexl.common.annotation.NullOrNotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;

@Validated
@ConfigurationProperties(prefix = "credential")
public record CredentialConfig(

        @Nullable
        @NullOrNotBlank
        String phone,

        @Nullable
        @NullOrNotBlank
        String code

) {
}
