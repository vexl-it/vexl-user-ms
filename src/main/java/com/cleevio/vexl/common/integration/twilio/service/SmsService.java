package com.cleevio.vexl.common.integration.twilio.service;

public interface SmsService {

    	void sendMessage(String codeToSend, String phoneNumber);
}
