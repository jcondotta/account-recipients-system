package com.jcondotta.account_recipients.domain.recipient.value_objects;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record RecipientName(String value) {

    static final int MAX_LENGTH = 50;

    static final String NAME_NOT_NULL = "recipient name must not be null";
    static final String NAME_NOT_BLANK = "recipient name must not be blank";
    static final String NAME_TOO_LONG = "recipient name must not exceed " + MAX_LENGTH + " characters";

    public RecipientName {
        Objects.requireNonNull(value, NAME_NOT_NULL);

        var normalized = StringUtils.normalizeSpace(value).trim();

        if (StringUtils.isBlank(normalized)) {
            throw new IllegalArgumentException(NAME_NOT_BLANK);
        }

        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(NAME_TOO_LONG);
        }

        value = normalized;
    }

    public static RecipientName of(String value) {
        return new RecipientName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}