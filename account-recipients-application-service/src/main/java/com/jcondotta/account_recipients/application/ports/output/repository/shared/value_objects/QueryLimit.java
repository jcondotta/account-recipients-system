package com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects;

import static java.util.Objects.requireNonNull;

public record QueryLimit(Integer value) {

    public static final String VALUE_NOT_NULL_MESSAGE = "value must not be null";
    public static final String INVALID_RANGE_MESSAGE_TEMPLATE = "value must be between %d and %d but was: %d";

    private static final int MIN = 1;
    private static final int MAX = 20;

    public QueryLimit {
        requireNonNull(value, VALUE_NOT_NULL_MESSAGE);

        if (value < MIN || value > MAX) {
            throw new IllegalArgumentException(String.format(INVALID_RANGE_MESSAGE_TEMPLATE, MIN, MAX, value));
        }
    }

    public static QueryLimit of(Integer value) {
        return new QueryLimit(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}