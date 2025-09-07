package com.jcondotta.account_recipients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication//(scanBasePackages = "com.jcondotta.account_recipients")
//@ConfigurationPropertiesScan(basePackages = "com.jcondotta.account_recipients")
public class AccountRecipientApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountRecipientApplication.class, args);
    }
}