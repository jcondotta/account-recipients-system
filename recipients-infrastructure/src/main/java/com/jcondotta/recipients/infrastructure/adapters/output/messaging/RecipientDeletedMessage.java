package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;
import java.util.UUID;

public record RecipientDeletedMessage(
    UUID eventId,
    UUID recipientId,
    UUID bankAccountId,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime occurredAt) {
}