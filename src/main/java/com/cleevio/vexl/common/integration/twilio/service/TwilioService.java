package com.cleevio.vexl.common.integration.twilio.service;

import com.cleevio.vexl.common.integration.twilio.config.TwilioConfig;
import com.cleevio.vexl.module.user.exception.InvalidPhoneNumberException;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwilioService implements SmsService {

    private static final int INVALID_NUMBER = 21211;
    private static final int NOT_NUMBER = 21614;
    private static final String SMS_TEXT = "Your verification code for Vexl is: ";
    private final TwilioConfig twilioConfig;

    @Override
    public String sendMessage(String phoneNumber) {
        try {

            Verification v = Verification.creator(
                    twilioConfig.getVerifyServiceSid(),
                    phoneNumber,
                    "sms"
            ).create();

            log.info("Sms successfully sent to " + phoneNumber);
            return v.getSid();
        } catch (ApiException ex) {
            if (ex.getCode() == INVALID_NUMBER || ex.getCode() == NOT_NUMBER) {
                throw new InvalidPhoneNumberException();
            }
            log.error("Failed to send sms to number {}", phoneNumber, ex);

            throw ex;
        }
    }

    @Override
    public Boolean verifyMessage(final String phoneNumber, final String code) {
        try {
            VerificationCheck check = VerificationCheck.creator(twilioConfig.getVerifyServiceSid())
                    .setTo(phoneNumber)
                    .setCode(code)
                    .create();

            return check.getStatus().toLowerCase(Locale.ROOT).equals(
                    Verification.Status.APPROVED.toString().toLowerCase(Locale.ROOT)
            );
        } catch (ApiException ex) {
            log.error("Failed to verify sms to number {}", phoneNumber, ex);
            throw ex;
        }
    }
}
