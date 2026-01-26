package com.jcondotta.account_recipients.infrastructure.adapters.output.messaging;

import java.time.Instant;

public record RecipientDeletedMessage(String recipientId, String bankAccountId, Instant occurredAt, String occurredAtZone) {
}