package com.jcondotta.account_recipients.application.usecase.shared.value_objects;

import java.util.Objects;
import java.util.UUID;

public record IdempotencyKey(UUID value) {

    public IdempotencyKey {
        Objects.requireNonNull(value, "IdempotencyKey value must not be null");
    }

    public static IdempotencyKey of(UUID uuid) {
        return new IdempotencyKey(uuid);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
