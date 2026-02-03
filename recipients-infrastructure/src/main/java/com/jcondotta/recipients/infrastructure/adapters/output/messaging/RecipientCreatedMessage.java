package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

public record RecipientCreatedMessage(
    String recipientId,
    String recipientName,
    String bankAccountId,
    String iban,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime occurredAt) {
}