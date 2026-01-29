package com.jcondotta.account_recipients.infrastructure.adapters.output.messaging;

import static java.util.Objects.requireNonNull;

public record EventEnvelope<T>(EventMetadata metadata, T payload) {

  public static final String METADATA_NOT_NULL_MESSAGE = "metadata must not be null.";
  public static final String PAYLOAD_NOT_NULL_MESSAGE = "metadata must not be null.";

  public EventEnvelope {
    requireNonNull(metadata, METADATA_NOT_NULL_MESSAGE);
    requireNonNull(payload, PAYLOAD_NOT_NULL_MESSAGE);
  }

  public static <T> EventEnvelope<T> of(EventMetadata metadata, T payload) {
    return new EventEnvelope<>(metadata, payload);
  }

//  @JsonCreator
//  public EventEnvelope(
//      @JsonProperty("metadata") EventMetadata metadata,
//      @JsonProperty("payload") T payload
//  ) {
//    this.metadata = metadata;
//    this.payload = payload;
//  }
}