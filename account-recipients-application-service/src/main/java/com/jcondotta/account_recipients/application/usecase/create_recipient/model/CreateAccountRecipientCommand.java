package com.jcondotta.account_recipients.application.usecase.create_recipient.model;

import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;

public record CreateAccountRecipientCommand(BankAccountId bankAccountId, RecipientName recipientName, Iban iban, ZonedDateTime createdAt) {

    public CreateAccountRecipientCommand {
        requireNonNull(bankAccountId, "bankAccountId must not be null");
        requireNonNull(recipientName, "recipientName must not be null");
        requireNonNull(iban, "iban must not be null");
        requireNonNull(createdAt, "createdAt must not be null");
    }

    public static CreateAccountRecipientCommand of(BankAccountId bankAccountId, RecipientName recipientName, Iban iban, ZonedDateTime createdAt) {
        return new CreateAccountRecipientCommand(bankAccountId, recipientName, iban, createdAt);
    }
}