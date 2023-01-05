package com.cleevio.vexl.common.integration.twilio.service;

public interface SmsService {

    	String sendMessage(String phoneNumber);
    	Boolean verifyMessage(String phoneNumber, String sid);
}
