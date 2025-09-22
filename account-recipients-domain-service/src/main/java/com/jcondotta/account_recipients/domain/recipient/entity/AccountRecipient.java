package com.jcondotta.account_recipients.domain.recipient.entity;

import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;

import java.time.Clock;
import java.time.ZonedDateTime;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public record AccountRecipient(
    AccountRecipientId accountRecipientId,
    BankAccountId bankAccountId,
    RecipientName recipientName,
    Iban iban,
    ZonedDateTime createdAt
) {

    public AccountRecipient {
        requireNonNull(accountRecipientId, "accountRecipientId must not be null");
        requireNonNull(bankAccountId, "bankAccountId must not be null");
        requireNonNull(recipientName, "recipientName must not be null");
        requireNonNull(iban, "iban must not be null");
        requireNonNull(createdAt, "createdAt must not be null");
    }

    public static AccountRecipient of(AccountRecipientId accountRecipientId,
                                      BankAccountId bankAccountId,
                                      RecipientName recipientName,
                                      Iban iban,
                                      ZonedDateTime createdAt) {
        return new AccountRecipient(accountRecipientId, bankAccountId, recipientName, iban, createdAt);
    }
}