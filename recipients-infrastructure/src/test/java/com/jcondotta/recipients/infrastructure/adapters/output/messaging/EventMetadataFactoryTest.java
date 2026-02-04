package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EventMetadataFactoryTest {

  private final EventMetadataFactory factory = new EventMetadataFactory();

  @Test
  void shouldCreateEventMetadata_whenValuesAreValid() {
    var before = Instant.now();

    var eventMetadata = factory.create();

    var after = Instant.now();

    assertThat(eventMetadata).isNotNull();

    assertThat(eventMetadata.publishedAt())
        .isNotNull()
        .isAfterOrEqualTo(before)
        .isBeforeOrEqualTo(after);
  }
}