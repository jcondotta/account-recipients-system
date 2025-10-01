package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity;

import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.util.UUID;

public final class AccountRecipientEntityKey {

    private static final String PK_PREFIX = "ACCOUNT_OWNER#";
    private static final String SK_PREFIX = "ACCOUNT_RECIPIENT#";

    private AccountRecipientEntityKey() {}

    public static String partitionKey(UUID bankAccountId) {
        return PK_PREFIX + bankAccountId.toString();
    }

    public static String partitionKey(BankAccountId bankAccountId) {
        return partitionKey(bankAccountId.value());
    }

    public static String sortKey(UUID accountRecipientId) {
        return SK_PREFIX + accountRecipientId.toString();
    }

    public static String sortKey(AccountRecipientId accountRecipientId) {
        return sortKey(accountRecipientId.value());
    }

    public static BankAccountId extractBankAccountId(String partitionKey) {
        if (partitionKey == null || !partitionKey.startsWith(PK_PREFIX)) {
            throw new IllegalArgumentException("Invalid partitionKey: " + partitionKey);
        }
        var bankAccountId = partitionKey.replace(PK_PREFIX, "");
        return BankAccountId.of(UUID.fromString(bankAccountId));
    }

    public static AccountRecipientId extractAccountRecipientId(String sortKey) {
        if (sortKey == null || !sortKey.startsWith(SK_PREFIX)) {
            throw new IllegalArgumentException("Invalid sortKey: " + sortKey);
        }
        var accountRecipientId = sortKey.replace(SK_PREFIX, "");
        return AccountRecipientId.of(UUID.fromString(accountRecipientId));
    }
}