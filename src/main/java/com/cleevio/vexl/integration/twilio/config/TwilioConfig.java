package com.cleevio.vexl.integration.twilio.config;

import com.twilio.Twilio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class TwilioConfig {

    @Value("${twilio.sid}")
    protected String sid;

    @Value("${twilio.token}")
    protected String token;

    @Value("${twilio.key}")
    protected String key;

    @Value("${twilio.secret}")
    protected String secret;

    @Value("${twilio.phone}")
    protected String phone;

    @PostConstruct
    public void init() {
        if (!sid.isEmpty() && !token.isEmpty() && !phone.isEmpty()) {
            Twilio.init(sid, token);

            log.info("Twilio initialized");
        } else {
            log.error("Twilio cannot be initialized");
        }
    }

    public String getSid() {
        return sid;
    }

    public String getToken() {
        return token;
    }

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }

    public String getPhone() {
        return phone;
    }
}
