package com.jcondotta.recipients.infrastructure.config.i18n;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class MessageSourceConfig {

  @Bean
  public MessageSource errorMessageSource() {
    var messageSource = new ResourceBundleMessageSource();

    messageSource.setBasename("i18n/exceptions/exceptions");
    messageSource.setDefaultEncoding("UTF-8");
    messageSource.setUseCodeAsDefaultMessage(false);
    messageSource.setDefaultLocale(Locale.ENGLISH);

    return messageSource;
  }
}
