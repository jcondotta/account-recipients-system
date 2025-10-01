package com.jcondotta.account_recipients.common.factory;

import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import org.iban4j.Iban;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;

public class AccountRecipientEntityTestFactory {

    private static final Clock DEFAULT_CLOCK = Clock.systemUTC();

    public static AccountRecipientEntity create(UUID accountRecipientId, UUID bankAccountId, String recipientName, String iban, Clock clock) {
        return new AccountRecipientEntity(accountRecipientId, bankAccountId, recipientName, iban, ZonedDateTime.now(clock));
    }

    public static AccountRecipientEntity create(UUID bankAccountId, String recipientName, String iban, Clock clock) {
        return create(UUID.randomUUID(), bankAccountId, recipientName, iban, clock);
    }

    public static AccountRecipientEntity create(UUID bankAccountId, String recipientName, Clock clock) {
        return create(UUID.randomUUID(), bankAccountId, recipientName, Iban.random().toString(), clock);
    }

    public static AccountRecipientEntity create(UUID bankAccountId, String recipientName) {
        return create(UUID.randomUUID(), bankAccountId, recipientName, Iban.random().toString(), DEFAULT_CLOCK);
    }
}