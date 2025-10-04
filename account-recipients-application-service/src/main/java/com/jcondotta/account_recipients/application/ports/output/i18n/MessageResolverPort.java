package com.jcondotta.account_recipients.application.ports.output.i18n;

import java.util.Locale;

@FunctionalInterface
public interface MessageResolverPort {

    String resolveMessage(String messageCode, Object[] args, Locale locale);
}