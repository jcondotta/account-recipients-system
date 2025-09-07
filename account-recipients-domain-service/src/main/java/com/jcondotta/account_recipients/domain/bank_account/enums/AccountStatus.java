package com.jcondotta.account_recipients.domain.bank_account.enums;

public enum AccountStatus {
    ACTIVE, CANCELLED, PENDING;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }

    public boolean isPending() {
        return this == PENDING;
    }
}