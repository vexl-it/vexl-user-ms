package com.cleevio.vexl.module.file.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {

    @NotNull
    @NotEmpty
    private String extension;

    @NotNull
    @NotEmpty
    private String data;
}
