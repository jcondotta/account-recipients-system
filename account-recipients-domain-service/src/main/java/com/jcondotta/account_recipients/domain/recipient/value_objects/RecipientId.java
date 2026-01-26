package com.jcondotta.account_recipients.domain.recipient.value_objects;

import java.util.Objects;
import java.util.UUID;

public record RecipientId(UUID value) {

    public static final String ID_NOT_NULL_MESSAGE = "recipient id value must not be null.";

    public RecipientId {
        Objects.requireNonNull(value, ID_NOT_NULL_MESSAGE);
    }

    public static RecipientId of(UUID value) {
        return new RecipientId(value);
    }

    public static RecipientId newId() {
        return new RecipientId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
