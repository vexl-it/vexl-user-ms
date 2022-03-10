package com.cleevio.vexl.module.sms.service;

import com.cleevio.vexl.module.user.entity.UserVerification;
import com.cleevio.vexl.module.user.exception.UserPhoneInvalidException;

public interface SmsService {

    	void sendMessage(UserVerification verification, String phoneNumber) throws UserPhoneInvalidException;
}
