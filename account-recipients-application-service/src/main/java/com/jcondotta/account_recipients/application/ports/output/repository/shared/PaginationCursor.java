package com.jcondotta.account_recipients.application.ports.output.repository.shared;

import java.util.Objects;

public record PaginationCursor(String value) {

    static final String VALUE_NOT_NULL_MESSAGE = "PaginationCursor value must not be null";

    public PaginationCursor {
        Objects.requireNonNull(value, VALUE_NOT_NULL_MESSAGE);
    }

    public static PaginationCursor of(String value) {
        return new PaginationCursor(value);
    }

    @Override
    public String toString() {
        return value;
    }
}