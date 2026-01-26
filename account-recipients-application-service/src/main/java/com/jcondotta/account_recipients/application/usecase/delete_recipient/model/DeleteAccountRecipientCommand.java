package com.jcondotta.account_recipients.application.usecase.delete_recipient.model;

import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.util.Objects;

public record DeleteAccountRecipientCommand(BankAccountId bankAccountId, RecipientId recipientId) {

  public DeleteAccountRecipientCommand {
    Objects.requireNonNull(bankAccountId, "bankAccountId must not be null");
    Objects.requireNonNull(recipientId, "recipientId must not be null");
  }

  public static DeleteAccountRecipientCommand of(BankAccountId bankAccountId, RecipientId recipientId) {
    return new DeleteAccountRecipientCommand(bankAccountId, recipientId);
  }
}
