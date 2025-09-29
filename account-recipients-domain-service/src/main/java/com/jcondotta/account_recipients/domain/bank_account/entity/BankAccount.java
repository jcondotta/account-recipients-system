package com.jcondotta.account_recipients.domain.bank_account.entity;

import com.jcondotta.account_recipients.domain.bank_account.enums.AccountStatus;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import static java.util.Objects.requireNonNull;

public record BankAccount(BankAccountId bankAccountId, AccountStatus accountStatus) {

    static final String BANK_ACCOUNT_ID_NOT_NULL = "bankAccountId must not be null.";
    static final String ACCOUNT_STATUS_NOT_NULL = "accountStatus must not be null.";

    public BankAccount {
        requireNonNull(bankAccountId, BANK_ACCOUNT_ID_NOT_NULL);
        requireNonNull(accountStatus, ACCOUNT_STATUS_NOT_NULL);
    }

    public static BankAccount of(BankAccountId bankAccountId, AccountStatus accountStatus) {
        return new BankAccount(bankAccountId, accountStatus);
    }

    public boolean isActive() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    public boolean isPending() {
        return accountStatus == AccountStatus.PENDING;
    }

    public boolean isCancelled() {
        return accountStatus == AccountStatus.CANCELLED;
    }
}