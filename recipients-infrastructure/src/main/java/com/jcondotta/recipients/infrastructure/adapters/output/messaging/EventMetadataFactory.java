package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.recipients.application.usecase.shared.value_objects.IdempotencyKey;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
public class EventMetadataFactory {

  public EventMetadata create(IdempotencyKey idempotencyKey) {
    Objects.requireNonNull(idempotencyKey, "idempotency key must not be null");

    return new EventMetadata(idempotencyKey.value(), Instant.now());
  }
}

