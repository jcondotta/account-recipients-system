package com.jcondotta.account_recipients.application.ports.output.cache;

import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.util.Objects;

public record AccountRecipientsRootCacheKey(BankAccountId bankAccountId) implements AccountRecipientsCacheKey {

    public static final String BANK_ACCOUNT_ID_NOT_NULL_MESSAGE = "bank account id must not be null";

    public AccountRecipientsRootCacheKey {
        Objects.requireNonNull(bankAccountId, BANK_ACCOUNT_ID_NOT_NULL_MESSAGE);
    }

    public static AccountRecipientsRootCacheKey of(BankAccountId bankAccountId) {
        return new AccountRecipientsRootCacheKey(bankAccountId);
    }

    @Override
    public String value() {
        return rootPrefix();
    }

    @Override
    public String toString() {
        return value();
    }
}