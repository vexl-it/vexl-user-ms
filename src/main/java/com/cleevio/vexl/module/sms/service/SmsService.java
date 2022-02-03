package com.cleevio.vexl.module.sms.service;

import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.entity.UserVerification;

public interface SmsService {

    	void sendMessage(UserVerification verification, String phoneNumber);
}
