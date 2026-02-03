package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.recipients.application.usecase.shared.value_objects.IdempotencyKey;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventMetadataFactoryTest {

  private final EventMetadataFactory factory = new EventMetadataFactory();

  @Test
  void shouldCreateEventMetadataWithIdempotencyKeyAndPublishedAt_whenValuesAreValid() {
    var idempotencyKey = IdempotencyKey.newKey();
    var before = Instant.now();

    var eventMetadata = factory.create(idempotencyKey);

    var after = Instant.now();

    assertThat(eventMetadata).isNotNull();
    assertThat(eventMetadata.idempotencyKey()).isEqualTo(idempotencyKey.value());

    assertThat(eventMetadata.publishedAt())
        .isNotNull()
        .isAfterOrEqualTo(before)
        .isBeforeOrEqualTo(after);
  }

  @Test
  void shouldThrowNullPointerException_whenIdempotencyKeyIsNull() {
    assertThatThrownBy(() -> factory.create(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("idempotency key must not be null");
  }
}