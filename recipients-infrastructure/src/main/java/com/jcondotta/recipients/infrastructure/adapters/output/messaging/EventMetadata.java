package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.Objects;

public record EventMetadata(
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
        timezone = "UTC"
    )
    Instant publishedAt
) {

    public static final String PUBLISHED_AT_NOT_NULL_MESSAGE = "publishedAt must not be null.";

    public EventMetadata {
        Objects.requireNonNull(publishedAt, PUBLISHED_AT_NOT_NULL_MESSAGE);
    }

    public static EventMetadata newEventMetadata() {
        return new EventMetadata(Instant.now());
    }
}
