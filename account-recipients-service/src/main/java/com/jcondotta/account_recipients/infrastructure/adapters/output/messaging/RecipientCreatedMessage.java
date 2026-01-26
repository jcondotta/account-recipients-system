package com.jcondotta.account_recipients.infrastructure.adapters.output.messaging;

import java.time.Instant;

public record RecipientCreatedMessage(
    String recipientId,
    String recipientName,
    String bankAccountId,
    String iban,
    Instant occurredAt,
    String occurredAtZone
) {
}