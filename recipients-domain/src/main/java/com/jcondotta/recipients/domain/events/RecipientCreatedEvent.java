package com.jcondotta.recipients.domain.events;

import com.jcondotta.recipients.domain.value_objects.*;

import java.time.ZonedDateTime;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record RecipientCreatedEvent(
    EventId eventId,
    RecipientId recipientId,
    RecipientName recipientName,
    BankAccountId bankAccountId,
    Iban iban,
    ZonedDateTime occurredAt
) implements RecipientEvent {

  public RecipientCreatedEvent {
    requireNonNull(eventId, "eventId must not be null");
    requireNonNull(recipientId, "recipientId must not be null");
    requireNonNull(recipientName, "recipientName must not be null");
    requireNonNull(bankAccountId, "bankAccountId must not be null");
    requireNonNull(iban, "iban must not be null");
    requireNonNull(occurredAt, "occurredAt must not be null");
  }

  public static RecipientCreatedEvent of(RecipientId recipientId, RecipientName recipientName, BankAccountId bankAccountId, Iban iban, ZonedDateTime occurredAt) {
    return new RecipientCreatedEvent(
        EventId.newEventId(),
        recipientId,
        recipientName,
        bankAccountId,
        iban,
        occurredAt
    );
  }
}