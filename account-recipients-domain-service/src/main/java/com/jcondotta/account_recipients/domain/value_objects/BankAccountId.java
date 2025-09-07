package com.jcondotta.account_recipients.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

public record BankAccountId(UUID value) {

    public static final String ID_NOT_NULL_MESSAGE = "bank account id value must not be null.";

    public BankAccountId {
        Objects.requireNonNull(value, ID_NOT_NULL_MESSAGE);
    }

    public static BankAccountId of(UUID value) {
        return new BankAccountId(value);
    }
}
