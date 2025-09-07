package com.jcondotta.account_recipients.infrastructure.config.clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {

    @Bean
    public Clock systemClockUTC(){
        return Clock.systemUTC();
    }
}