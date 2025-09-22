package com.jcondotta.account_recipients.infrastructure.adapters.output.i18n;

import com.jcondotta.account_recipients.application.ports.output.i18n.MessageResolverPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MessageResolverAdapter implements MessageResolverPort {

    private final ResourceBundleMessageSource messageSource;

    @Override
    public Optional<String> resolveMessage(String messageCode, Object[] args, Locale locale) {
        return Optional.ofNullable(
            messageSource.getMessage(messageCode, args, messageCode, locale)
        );
    }
}