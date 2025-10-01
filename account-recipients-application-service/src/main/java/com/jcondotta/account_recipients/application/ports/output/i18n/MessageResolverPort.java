package com.jcondotta.account_recipients.application.ports.output.i18n;

import java.util.Locale;
import java.util.Objects;

@FunctionalInterface
public interface MessageResolverPort {

    String resolveMessage(String messageCode, Object[] args, Locale locale);

//    /**
//     * Conveniência: varargs em vez de array.
//     */
//    default String resolveMessage(String messageCode, Locale locale, Object... args) {
//        return resolveMessage(
//            Objects.requireNonNull(messageCode, "MessageResolver can't resolve a null messageCode"),
//            (args == null ? new Object[0] : args),
//            Objects.requireNonNull(locale, "locale")
//        );
//    }
}