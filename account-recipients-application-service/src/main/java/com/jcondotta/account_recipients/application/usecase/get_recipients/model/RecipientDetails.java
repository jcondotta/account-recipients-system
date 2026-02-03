package com.jcondotta.account_recipients.application.usecase.get_recipients.model;

import com.jcondotta.account_recipients.domain.value_objects.Iban;
import com.jcondotta.account_recipients.domain.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;

import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;

public record RecipientDetails(
    RecipientId recipientId,
    BankAccountId bankAccountId,
    RecipientName recipientName,
    Iban iban,
    ZonedDateTime createdAt) {

  public RecipientDetails {
    requireNonNull(recipientId, "recipientId must not be null");
    requireNonNull(bankAccountId, "bankAccountId must not be null");
    requireNonNull(recipientName, "recipientName must not be null");
    requireNonNull(iban, "iban must not be null");
    requireNonNull(createdAt, "createdAt must not be null");
  }

  public static RecipientDetails of(
      RecipientId recipientId,
      BankAccountId bankAccountId,
      RecipientName recipientName,
      Iban iban,
      ZonedDateTime createdAt) {
    return new RecipientDetails(
        recipientId, bankAccountId, recipientName, iban, createdAt);
  }
}
