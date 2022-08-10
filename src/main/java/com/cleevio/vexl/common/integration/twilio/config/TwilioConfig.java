package com.cleevio.vexl.common.integration.twilio.config;

import com.twilio.Twilio;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TwilioConfig {

    @Getter
    private final String phone;

    public TwilioConfig(@Value("${twilio.sid}") String sid,
                        @Value("${twilio.token}") String token,
                        @Value("${twilio.phone}") String phone) {
        this.phone = phone;

        if (!sid.isEmpty() && !token.isEmpty() && !phone.isEmpty()) {
            Twilio.init(sid, token);

            log.info("Twilio initialized");
        } else {
            log.error("Twilio cannot be initialized");
        }
    }
}
