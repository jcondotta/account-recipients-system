package com.jcondotta.account_recipients.infrastructure.config.clock;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

  @Bean
  public Clock systemDefaultZone() {
    return Clock.systemDefaultZone();
  }
}
