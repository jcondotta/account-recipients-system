package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

public record RecipientDeletedMessage(
    String recipientId,
    String bankAccountId,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime occurredAt) {
}