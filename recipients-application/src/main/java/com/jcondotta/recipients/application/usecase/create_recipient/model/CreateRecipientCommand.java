package com.jcondotta.recipients.application.usecase.create_recipient.model;

import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.Iban;
import com.jcondotta.recipients.domain.value_objects.RecipientName;

import static java.util.Objects.requireNonNull;

public record CreateRecipientCommand(BankAccountId bankAccountId, RecipientName recipientName, Iban iban) {

  public CreateRecipientCommand {
    requireNonNull(bankAccountId, "bankAccountId must not be null");
    requireNonNull(recipientName, "recipientName must not be null");
    requireNonNull(iban, "iban must not be null");
  }

  public static CreateRecipientCommand of(BankAccountId bankAccountId, RecipientName recipientName, Iban iban) {
    return new CreateRecipientCommand(bankAccountId, recipientName, iban);
  }
}
