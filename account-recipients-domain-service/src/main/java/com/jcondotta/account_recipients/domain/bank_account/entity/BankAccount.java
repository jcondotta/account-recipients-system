package com.jcondotta.account_recipients.domain.bank_account.entity;

import com.jcondotta.account_recipients.domain.bank_account.enums.AccountStatus;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientDeletedEvent;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientEvent;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class BankAccount {

    static final String BANK_ACCOUNT_ID_NOT_NULL = "bankAccountId must not be null.";
    static final String ACCOUNT_STATUS_NOT_NULL = "accountStatus must not be null.";

    private final BankAccountId bankAccountId;
    private final AccountStatus accountStatus;

    private final List<RecipientEvent> recipientEvents = new ArrayList<>();

    private BankAccount(BankAccountId id, AccountStatus status) {
        this.bankAccountId = requireNonNull(id, BANK_ACCOUNT_ID_NOT_NULL);
        this.accountStatus = requireNonNull(status, ACCOUNT_STATUS_NOT_NULL);
    }

    public static BankAccount restore(BankAccountId id, AccountStatus status) {
        return new BankAccount(id, status);
    }

    public AccountRecipient createRecipient(RecipientName name, Iban iban, Clock clock) {
        if (!isActive()) {
            throw new IllegalStateException("Cannot create recipient for non-active account");
        }

        var accountRecipient = AccountRecipient.create(bankAccountId, name, iban, clock);

        recipientEvents.add(
            RecipientCreatedEvent.of(
                accountRecipient.getRecipientId(),
                accountRecipient.getRecipientName(),
                accountRecipient.getBankAccountId(),
                accountRecipient.getIban(),
                accountRecipient.getCreatedAt()
            ));

        return accountRecipient;
    }

    public void deleteRecipient(AccountRecipient accountRecipient, Clock clock) {
        if (!this.bankAccountId.equals(accountRecipient.getBankAccountId())) {
            throw new IllegalStateException("Recipient does not belong to this account");
        }

        if (!this.isActive()) {
            throw new IllegalStateException("Cannot delete recipient for non-active account");
        }

        accountRecipient.delete(clock);
        recipientEvents.add(
            RecipientDeletedEvent.of(
                accountRecipient.getRecipientId(),
                accountRecipient.getBankAccountId(),
                accountRecipient.getDeletedAt()
            )
        );
    }

    public List<RecipientEvent> pullRecipientEvents() {
        var events = List.copyOf(recipientEvents);
        recipientEvents.clear();
        return events;
    }

    public BankAccountId getBankAccountId() {
        return bankAccountId;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankAccount other)) return false;
        return bankAccountId.equals(other.bankAccountId);
    }

    @Override
    public int hashCode() {
        return bankAccountId.hashCode();
    }
}