package com.jcondotta.account_recipients.domain.recipient.value_objects;

import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

import java.util.Objects;

public record Iban(String value) {

    public static final String IBAN_NOT_NULL_MESSAGE = "IBAN must not be null.";
    public static final String IBAN_INVALID_MESSAGE = "IBAN format is invalid.";

    public Iban {
        Objects.requireNonNull(value, IBAN_NOT_NULL_MESSAGE);

        String sanitized = value.replaceAll("\\s+", "").toUpperCase();
        if (!IBANCheckDigit.IBAN_CHECK_DIGIT.isValid(sanitized)) {
            throw new IllegalArgumentException(IBAN_INVALID_MESSAGE);
        }

        value = sanitized;
    }

    public static Iban of(String value) {
        return new Iban(value);
    }

    @Override
    public String toString() {
        return value;
    }
}