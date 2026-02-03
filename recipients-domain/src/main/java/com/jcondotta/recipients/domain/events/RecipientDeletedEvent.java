package com.jcondotta.recipients.domain.events;

import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.RecipientId;

import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;

public record RecipientDeletedEvent(RecipientId recipientId, BankAccountId bankAccountId, ZonedDateTime occurredAt)
    implements RecipientEvent {

  public RecipientDeletedEvent {
    requireNonNull(recipientId, "recipientId must not be null");
    requireNonNull(bankAccountId, "bankAccountId must not be null");
    requireNonNull(occurredAt, "occurredAt must not be null");
  }

  public static RecipientDeletedEvent of(RecipientId recipientId, BankAccountId bankAccountId, ZonedDateTime occurredAt) {
    return new RecipientDeletedEvent(
        recipientId,
        bankAccountId,
        occurredAt
    );
  }
}
