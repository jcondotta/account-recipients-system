package com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model;

import static java.util.Objects.requireNonNull;

public record RecipientNamePrefix(String value) {

    public static final String VALUE_NOT_NULL_MESSAGE = "value must not be null";
    public static final String VALUE_NOT_BLANK_MESSAGE = "value must not be blank";

    public RecipientNamePrefix {
        requireNonNull(value, VALUE_NOT_NULL_MESSAGE);
        if (value.isBlank()) {
            throw new IllegalArgumentException(VALUE_NOT_BLANK_MESSAGE);
        }
    }

    public static RecipientNamePrefix of(String value) {
        return new RecipientNamePrefix(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
