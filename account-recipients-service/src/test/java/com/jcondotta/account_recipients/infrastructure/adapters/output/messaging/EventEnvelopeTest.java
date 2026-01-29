package com.jcondotta.account_recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventEnvelopeTest {

  private static final IdempotencyKey IDEMPOTENCY_KEY = IdempotencyKey.newKey();
  private static final String TEST_PAYLOAD = "test-payload";

  @Test
  void shouldCreateEventEnvelopeWithMetadataAndPayload_whenValuesAreValid() {
    var eventMetadata = EventMetadata.of(IDEMPOTENCY_KEY.value());
    var eventEnvelope = EventEnvelope.of(eventMetadata, TEST_PAYLOAD);

    assertThat(eventEnvelope).isNotNull();
    assertThat(eventEnvelope.metadata()).isEqualTo(eventMetadata);
    assertThat(eventEnvelope.payload()).isEqualTo(TEST_PAYLOAD);
  }

  @Test
  void shouldThrowNullPointerException_whenMetadataIsNull() {
    assertThatThrownBy(() -> EventEnvelope.of(null, TEST_PAYLOAD))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(EventEnvelope.METADATA_NOT_NULL_MESSAGE);
  }

  @Test
  void shouldThrowNullPointerException_whenPayloadIsNull() {
    var eventMetadata = EventMetadata.of(IDEMPOTENCY_KEY.value());

    assertThatThrownBy(() -> EventEnvelope.of(eventMetadata, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(EventEnvelope.PAYLOAD_NOT_NULL_MESSAGE);
  }
}