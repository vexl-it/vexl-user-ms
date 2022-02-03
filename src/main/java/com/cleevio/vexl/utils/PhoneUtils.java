package com.cleevio.vexl.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PhoneUtils {

    public static String trimAndDeleteSpacesFromPhoneNumber(String phoneNumber) {
        return phoneNumber.trim().replace(" ", "");
    }
}
