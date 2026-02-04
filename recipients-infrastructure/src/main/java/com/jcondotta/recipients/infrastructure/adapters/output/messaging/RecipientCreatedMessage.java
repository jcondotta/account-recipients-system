package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;
import java.util.UUID;

public record RecipientCreatedMessage(
    UUID eventId,
    UUID recipientId,
    String recipientName,
    UUID bankAccountId,
    String iban,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime occurredAt) {
}