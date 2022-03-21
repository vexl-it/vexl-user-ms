package com.cleevio.vexl.module.sms.service;

import com.cleevio.vexl.module.user.exception.UserPhoneInvalidException;

public interface SmsService {

    	void sendMessage(String codeToSend, String phoneNumber) throws UserPhoneInvalidException;
}
