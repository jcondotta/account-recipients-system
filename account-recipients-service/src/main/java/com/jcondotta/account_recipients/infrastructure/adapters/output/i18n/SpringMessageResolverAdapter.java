package com.jcondotta.account_recipients.infrastructure.adapters.output.i18n;

import com.jcondotta.account_recipients.application.ports.output.i18n.MessageResolverPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SpringMessageResolverAdapter implements MessageResolverPort {

    private final MessageSource messageSource;

    @Override
    public String resolveMessage(String messageCode, Object[] args, Locale locale) {
        Objects.requireNonNull(messageCode, "Message code must not be null");
        Objects.requireNonNull(locale, "Locale must not be null");

        Object[] safeArgs = (args == null) ? new Object[0] : args;
        return messageSource.getMessage(messageCode, safeArgs, messageCode, locale);
    }
}