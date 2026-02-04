package com.jcondotta.recipients.domain.entities;

import com.jcondotta.recipients.domain.enums.AccountStatus;
import com.jcondotta.recipients.domain.events.RecipientCreatedEvent;
import com.jcondotta.recipients.domain.events.RecipientDeletedEvent;
import com.jcondotta.recipients.domain.events.RecipientEvent;
import com.jcondotta.recipients.domain.exceptions.BankAccountNotActiveException;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.Iban;
import com.jcondotta.recipients.domain.value_objects.RecipientName;

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

  public Recipient createRecipient(RecipientName name, Iban iban, Clock clock) {
    if (!this.accountStatus.isActive()) {
      throw new BankAccountNotActiveException(
          "recipient.cannotBeCreated.bankAccountNotActive",
          "Recipient cannot be created",
          bankAccountId
      );
    }

    var recipient = Recipient.create(bankAccountId, name, iban, clock);

    recipientEvents.add(
        RecipientCreatedEvent.of(
            recipient.getRecipientId(),
            recipient.getRecipientName(),
            recipient.getBankAccountId(),
            recipient.getIban(),
            recipient.getCreatedAt()
        ));

    return recipient;
  }

  public void deleteRecipient(Recipient recipient, Clock clock) {
    if (!this.bankAccountId.equals(recipient.getBankAccountId())) {
      throw new IllegalStateException("Recipient does not belong to this account");
    }

    if (!this.accountStatus.isActive()) {
      throw new BankAccountNotActiveException(
          "recipient.cannotBeDeleted.bankAccountNotActive",
          "Recipient cannot be deleted",
          bankAccountId
      );
    }

    recipient.delete(clock);
    recipientEvents.add(
        RecipientDeletedEvent.of(
            recipient.getRecipientId(),
            recipient.getBankAccountId(),
            recipient.getDeletedAt()
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