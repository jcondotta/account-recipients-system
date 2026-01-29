package com.jcondotta.account_recipients.infrastructure.adapters.output.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record EventMetadata(
    UUID idempotencyKey,

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
        timezone = "UTC"
    )
    Instant publishedAt
) {

    public static final String IDEMPOTENCY_KEY_NOT_NULL_MESSAGE = "idempotencyKey must not be null.";
    public static final String PUBLISHED_AT_NOT_NULL_MESSAGE = "publishedAt must not be null.";

    public EventMetadata {
        Objects.requireNonNull(idempotencyKey, IDEMPOTENCY_KEY_NOT_NULL_MESSAGE);
        Objects.requireNonNull(publishedAt, PUBLISHED_AT_NOT_NULL_MESSAGE);
    }

    public static EventMetadata of(UUID idempotencyKey) {
        return new EventMetadata(idempotencyKey, Instant.now());
    }
}
