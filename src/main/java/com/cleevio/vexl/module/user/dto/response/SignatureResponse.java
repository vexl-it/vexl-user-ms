package com.cleevio.vexl.module.user.dto.response;

import com.cleevio.vexl.module.user.serializer.Base64Serializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignatureResponse {

    @Nullable
    @Schema(description = "Hash for phone number/facebookId in Base64 format")
    @JsonSerialize(using = Base64Serializer.class)
    public byte[] hash;

    @Nullable
    @Schema(description = "Signature in Base64 format")
    @JsonSerialize(using = Base64Serializer.class)
    public byte[] signature;

    @Schema(description = "Whether challenge is verified successfully")
    public boolean challengeVerified;

    public SignatureResponse(boolean challengeVerified) {
        this.hash = null;
        this.signature = null;
        this.challengeVerified = challengeVerified;
    }
}
