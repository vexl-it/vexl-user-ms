package com.cleevio.vexl.common.validator;

import com.cleevio.vexl.common.annotation.OnlyPng;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AllowedImageExtensionValidator implements ConstraintValidator<OnlyPng, String> {

    private static final String ALLOWED_IMAGE_EXTENSION = "png";

    @Override
    public void initialize(OnlyPng constraintAnnotation) {
    }

    @Override
    public boolean isValid(String extension, ConstraintValidatorContext constraintContext) {
        return ALLOWED_IMAGE_EXTENSION.equalsIgnoreCase(extension);
    }
}
