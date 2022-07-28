package com.cleevio.vexl.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PhoneUtils {

    public static String trimAndDeleteSpacesFromPhoneNumber(String phoneNumber) {
        return phoneNumber.trim().replace(" ", "");
    }
}
