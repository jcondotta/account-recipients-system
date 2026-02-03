package com.jcondotta.account_recipients.application.usecase.delete_recipient.model;

import com.jcondotta.account_recipients.domain.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;

import java.util.Objects;

public record DeleteRecipientCommand(BankAccountId bankAccountId, RecipientId recipientId) {

  public DeleteRecipientCommand {
    Objects.requireNonNull(bankAccountId, "bankAccountId must not be null");
    Objects.requireNonNull(recipientId, "recipientId must not be null");
  }

  public static DeleteRecipientCommand of(BankAccountId bankAccountId, RecipientId recipientId) {
    return new DeleteRecipientCommand(bankAccountId, recipientId);
  }
}
