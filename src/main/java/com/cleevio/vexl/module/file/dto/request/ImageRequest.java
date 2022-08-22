package com.cleevio.vexl.module.file.dto.request;

import com.cleevio.vexl.common.annotation.CheckValidImageExtension;
import com.cleevio.vexl.module.user.annotation.ValidAvatar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {

    @NotBlank
    @CheckValidImageExtension
    private String extension;

    @NotBlank
    @ValidAvatar
    private String data;
}
