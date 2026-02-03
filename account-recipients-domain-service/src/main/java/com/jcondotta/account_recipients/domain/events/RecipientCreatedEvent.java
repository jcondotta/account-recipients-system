package com.jcondotta.account_recipients.domain.events;

import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import com.jcondotta.account_recipients.domain.value_objects.Iban;
import com.jcondotta.account_recipients.domain.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.value_objects.RecipientName;

import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;

public record RecipientCreatedEvent(
    RecipientId recipientId,
    RecipientName recipientName,
    BankAccountId bankAccountId,
    Iban iban,
    ZonedDateTime occurredAt
) implements RecipientEvent {

  public RecipientCreatedEvent {
    requireNonNull(recipientId, "recipientId must not be null");
    requireNonNull(recipientName, "recipientName must not be null");
    requireNonNull(bankAccountId, "bankAccountId must not be null");
    requireNonNull(iban, "iban must not be null");
    requireNonNull(occurredAt, "occurredAt must not be null");
  }

  public static RecipientCreatedEvent of(RecipientId recipientId, RecipientName recipientName, BankAccountId bankAccountId, Iban iban, ZonedDateTime occurredAt) {
    return new RecipientCreatedEvent(
        recipientId,
        recipientName,
        bankAccountId,
        iban,
        occurredAt
    );
  }
}