package com.jcondotta.account_recipients.domain.recipient.events;

import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.shared.events.DomainEvent;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;

public record RecipientDeletedEvent(RecipientId recipientId, BankAccountId bankAccountId, Instant occurredAt,
                                    ZoneId occurredAtZone)
    implements DomainEvent {

  public RecipientDeletedEvent {
    requireNonNull(recipientId, "recipientId must not be null");
    requireNonNull(bankAccountId, "bankAccountId must not be null");
    requireNonNull(occurredAt, "occurredAt must not be null");
    requireNonNull(occurredAtZone, "occurredAtZone must not be null");
  }

  public static RecipientDeletedEvent of(RecipientId recipientId, BankAccountId bankAccountId, ZonedDateTime occurredAt) {
    return new RecipientDeletedEvent(
        recipientId,
        bankAccountId,
        occurredAt.toInstant(),
        occurredAt.getZone()
    );
  }

  public static RecipientDeletedEvent of(RecipientId recipientId, BankAccountId bankAccountId, Clock clock) {
    return new RecipientDeletedEvent(
        recipientId,
        bankAccountId,
        Instant.now(clock),
        clock.getZone()
    );
  }
}
