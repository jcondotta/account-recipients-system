package com.jcondotta.account_recipients.domain.recipient.events;

import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.events.DomainEvent;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;

public record RecipientCreatedEvent(
        RecipientId recipientId,
        RecipientName recipientName,
        BankAccountId bankAccountId,
        Iban iban,
        Instant occurredAt,
        ZoneId occurredAtZone
) implements DomainEvent {

    public RecipientCreatedEvent {
        requireNonNull(recipientId, "recipientId must not be null");
        requireNonNull(recipientName, "recipientName must not be null");
        requireNonNull(bankAccountId, "bankAccountId must not be null");
        requireNonNull(iban, "iban must not be null");
        requireNonNull(occurredAt, "occurredAt must not be null");
        requireNonNull(occurredAtZone, "occurredAtZone must not be null");
    }

    public static RecipientCreatedEvent of(RecipientId recipientId, RecipientName recipientName, BankAccountId bankAccountId, Iban iban, ZonedDateTime occurredAt) {
        requireNonNull(occurredAt, "occurredAt must not be null");

        return new RecipientCreatedEvent(
                recipientId,
                recipientName,
                bankAccountId,
                iban,
                occurredAt.toInstant(),
                occurredAt.getZone()
        );
    }
}