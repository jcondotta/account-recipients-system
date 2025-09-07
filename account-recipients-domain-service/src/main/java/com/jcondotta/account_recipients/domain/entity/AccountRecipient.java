package com.jcondotta.account_recipients.domain.entity;

import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import com.jcondotta.account_recipients.domain.value_objects.RecipientIban;
import com.jcondotta.account_recipients.domain.value_objects.RecipientName;

import java.time.Clock;
import java.time.ZonedDateTime;

public record AccountRecipient(BankAccountId bankAccountId, RecipientName recipientName, RecipientIban recipientIban, ZonedDateTime createdAt) {

    public static AccountRecipient of(BankAccountId bankAccountId, RecipientName recipientName, RecipientIban recipientIban, ZonedDateTime createdAt) {
        return new AccountRecipient(bankAccountId, recipientName, recipientIban, createdAt);
    }

    public static AccountRecipient of(BankAccountId bankAccountId, RecipientName recipientName, RecipientIban recipientIban, Clock clock) {
        return new AccountRecipient(bankAccountId, recipientName, recipientIban, ZonedDateTime.now(clock));
    }
}