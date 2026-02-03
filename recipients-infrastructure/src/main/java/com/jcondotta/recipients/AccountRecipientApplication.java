package com.jcondotta.recipients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.jcondotta.recipients")
@EnableFeignClients(basePackages = "com.jcondotta.recipients.infrastructure")
@ConfigurationPropertiesScan(basePackages = "com.jcondotta.recipients.infrastructure.properties")
public class AccountRecipientApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccountRecipientApplication.class, args);
  }
}

