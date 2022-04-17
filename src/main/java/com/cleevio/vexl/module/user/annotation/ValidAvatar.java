package com.cleevio.vexl.module.user.annotation;

import com.cleevio.vexl.module.user.validator.AvatarSizeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = {AvatarSizeValidator.class})
public @interface ValidAvatar {

    String message() default "avatar is exceeding maximum size";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
