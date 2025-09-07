package com.jcondotta.account_recipients.application.usecase.create_recipient.model;

import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import com.jcondotta.account_recipients.domain.value_objects.RecipientIban;
import com.jcondotta.account_recipients.domain.value_objects.RecipientName;

import java.time.Clock;
import java.time.ZonedDateTime;

public record CreateAccountRecipientCommand(BankAccountId bankAccountId, RecipientName recipientName, RecipientIban recipientIban, ZonedDateTime createdAt) {

    public static CreateAccountRecipientCommand of(BankAccountId bankAccountId, RecipientName recipientName, RecipientIban recipientIban, ZonedDateTime createdAt) {
        return new CreateAccountRecipientCommand(bankAccountId, recipientName, recipientIban, createdAt);
    }

    public static CreateAccountRecipientCommand of(BankAccountId bankAccountId, RecipientName recipientName, RecipientIban recipientIban, Clock clock) {
        return of(bankAccountId, recipientName, recipientIban, ZonedDateTime.now(clock));
    }
}