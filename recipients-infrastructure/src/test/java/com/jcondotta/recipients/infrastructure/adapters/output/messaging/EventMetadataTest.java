package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.recipients.application.usecase.shared.value_objects.IdempotencyKey;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventMetadataTest {

  private static final IdempotencyKey IDEMPOTENCY_KEY = IdempotencyKey.newKey();

  @Test
  void shouldCreateEventMetadataWithIdempotencyKeyAndPublishedAt_whenValuesAreValid() {
    var before = Instant.now();
    var eventMetadata = EventMetadata.of(IDEMPOTENCY_KEY.value());
    var after = Instant.now();

    assertThat(eventMetadata).isNotNull();
    assertThat(eventMetadata.idempotencyKey()).isEqualTo(IDEMPOTENCY_KEY.value());

    assertThat(eventMetadata.publishedAt())
        .isNotNull()
        .isAfterOrEqualTo(before)
        .isBeforeOrEqualTo(after);
  }

  @Test
  void shouldThrowNullPointerException_whenIdempotencyKeyIsNull() {
    assertThatThrownBy(() -> EventMetadata.of(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(EventMetadata.IDEMPOTENCY_KEY_NOT_NULL_MESSAGE);
  }

  @Test
  void shouldThrowNullPointerException_whenPublishedAtIsNull() {
    var idempotencyKeyUUID = IDEMPOTENCY_KEY.value();

    assertThatThrownBy(() -> new EventMetadata(idempotencyKeyUUID, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(EventMetadata.PUBLISHED_AT_NOT_NULL_MESSAGE);
  }
}
