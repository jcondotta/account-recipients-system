package com.jcondotta.account_recipients.application.usecase.delete_recipient.model;

import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.util.Objects;

public record DeleteAccountRecipientCommand(BankAccountId bankAccountId, AccountRecipientId accountRecipientId) {

    public DeleteAccountRecipientCommand {
        Objects.requireNonNull(bankAccountId, "bankAccountId must not be null");
        Objects.requireNonNull(accountRecipientId, "accountRecipientId must not be null");
    }

    public static DeleteAccountRecipientCommand of(BankAccountId bankAccountId, AccountRecipientId accountRecipientId) {
        return new DeleteAccountRecipientCommand(bankAccountId, accountRecipientId);
    }
}