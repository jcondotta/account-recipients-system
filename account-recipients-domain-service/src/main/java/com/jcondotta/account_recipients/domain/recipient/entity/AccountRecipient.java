package com.jcondotta.account_recipients.domain.recipient.entity;

import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;

import java.time.Clock;
import java.time.ZonedDateTime;

public record AccountRecipient(BankAccountId bankAccountId, RecipientName recipientName, Iban recipientIban, ZonedDateTime createdAt) {

    public static AccountRecipient of(BankAccountId bankAccountId, RecipientName recipientName, Iban recipientIban, ZonedDateTime createdAt) {
        return new AccountRecipient(bankAccountId, recipientName, recipientIban, createdAt);
    }

    public static AccountRecipient of(BankAccountId bankAccountId, RecipientName recipientName, Iban recipientIban, Clock clock) {
        return new AccountRecipient(bankAccountId, recipientName, recipientIban, ZonedDateTime.now(clock));
    }
}