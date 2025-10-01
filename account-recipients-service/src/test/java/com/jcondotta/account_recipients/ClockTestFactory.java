package com.jcondotta.account_recipients;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@Configuration
public class ClockTestFactory {

    public static final Clock TEST_CLOCK_FIXED = Clock.fixed(Instant.parse("2022-06-24T12:45:01Z"), ZoneOffset.UTC);

    @Bean
    @Primary
    public Clock systemClock(){
        return TEST_CLOCK_FIXED;
    }
}