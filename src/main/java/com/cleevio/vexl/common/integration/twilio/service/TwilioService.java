package com.cleevio.vexl.common.integration.twilio.service;

import com.cleevio.vexl.common.integration.twilio.config.TwilioConfig;
import com.cleevio.vexl.module.user.exception.UserPhoneInvalidException;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TwilioService implements SmsService {

    private final static int INVALID_NUMBER = 21211;
    private final static int NOT_NUMBER = 21211;
    private final TwilioConfig twilioConfig;

    @Override
    public void sendMessage(String codeToSend, String phoneNumber)
            throws UserPhoneInvalidException {
        try {
            Message.creator(
                            new PhoneNumber(phoneNumber),
                            new PhoneNumber(twilioConfig.getPhone()),
                            codeToSend)
                    .create();

            log.info("Sms successfully sent to " + phoneNumber);
        } catch (ApiException ex) {
            log.error("Failed to send sms to number {}", phoneNumber, ex);

            if (ex.getCode() == INVALID_NUMBER || ex.getCode() == NOT_NUMBER) {
                throw new UserPhoneInvalidException();
            }

            throw ex;
        }
    }
}