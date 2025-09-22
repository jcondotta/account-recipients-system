package com.jcondotta.account_recipients.domain.recipient.value_objects;

import java.util.Objects;
import java.util.UUID;

public record AccountRecipientId(UUID value) {

    public static final String ID_NOT_NULL_MESSAGE = "account recipient id value must not be null.";

    public AccountRecipientId {
        Objects.requireNonNull(value, ID_NOT_NULL_MESSAGE);
    }

    public static AccountRecipientId of(UUID value) {
        return new AccountRecipientId(value);
    }

    public static AccountRecipientId newId() {
        return new AccountRecipientId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
