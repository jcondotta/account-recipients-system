package com.jcondotta.recipients.application.execution;

import java.util.Objects;
import java.util.UUID;

public record IdempotencyKey(UUID value) {

  public IdempotencyKey {
    Objects.requireNonNull(value, "IdempotencyKey value must not be null");
  }

  public static IdempotencyKey newKey() {
    return new IdempotencyKey(UUID.randomUUID());
  }

  public static IdempotencyKey of(UUID uuid) {
    return new IdempotencyKey(uuid);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
