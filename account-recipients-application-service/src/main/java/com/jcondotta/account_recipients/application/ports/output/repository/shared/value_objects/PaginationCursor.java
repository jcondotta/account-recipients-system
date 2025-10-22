package com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects;

import java.util.Objects;

public record PaginationCursor(String value) {

    public static final String VALUE_NOT_NULL_MESSAGE = "PaginationCursor value must not be null";
    public static final String VALUE_NOT_BLANK_MESSAGE = "value must not be blank";

    public PaginationCursor {
        Objects.requireNonNull(value, VALUE_NOT_NULL_MESSAGE);
        if (value.isBlank()) {
            throw new IllegalArgumentException(VALUE_NOT_BLANK_MESSAGE);
        }
    }

    public static PaginationCursor of(String value) {
        return new PaginationCursor(value);
    }

    @Override
    public String toString() {
        return value;
    }
}