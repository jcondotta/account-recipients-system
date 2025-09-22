package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity;

import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

public final class AccountRecipientEntityKey {

    public static final String PK_TEMPLATE = "ACCOUNT_OWNER#%s";
    public static final String SK_TEMPLATE = "ACCOUNT_RECIPIENT#%s";

    private AccountRecipientEntityKey() {}

    public static String partitionKey(BankAccountId bankAccountId) {
        return PK_TEMPLATE.formatted(bankAccountId.value().toString());
    }

    public static String sortKey(AccountRecipientId accountRecipientId) {
        return SK_TEMPLATE.formatted(accountRecipientId.value().toString());
    }
}