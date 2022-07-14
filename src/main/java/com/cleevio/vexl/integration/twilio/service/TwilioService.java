package com.cleevio.vexl.integration.twilio.service;

import com.cleevio.vexl.integration.twilio.config.TwilioConfig;
import com.cleevio.vexl.module.sms.service.SmsService;
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

    private final TwilioConfig twilioConfig;

    @Override
    public void sendMessage(String codeToSend, String phoneNumber)
            throws UserPhoneInvalidException {
        log.info("Sending sms.");

        try {
            Message.creator(
                            new PhoneNumber(phoneNumber),
                            new PhoneNumber(twilioConfig.getPhone()),
                            codeToSend)
                    .create();
        } catch (ApiException ex) {

            if (ex.getCode() == 21211 || ex.getCode() == 21614) {
                throw new UserPhoneInvalidException();
            }

            throw ex;
        }
    }
}
