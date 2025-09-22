package com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model;

import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;

public record AccountRecipientDetails(
    AccountRecipientId accountRecipientId,
    BankAccountId bankAccountId,
    RecipientName recipientName,
    Iban iban,
    ZonedDateTime createdAt) {

    public AccountRecipientDetails {
        requireNonNull(accountRecipientId, "accountRecipientId must not be null");
        requireNonNull(bankAccountId, "bankAccountId must not be null");
        requireNonNull(recipientName, "recipientName must not be null");
        requireNonNull(iban, "iban must not be null");
        requireNonNull(createdAt, "createdAt must not be null");
    }

    public static AccountRecipientDetails of(AccountRecipientId accountRecipientId, BankAccountId bankAccountId, RecipientName recipientName, Iban iban, ZonedDateTime createdAt) {
        return new AccountRecipientDetails(accountRecipientId, bankAccountId, recipientName, iban, createdAt);
    }
}