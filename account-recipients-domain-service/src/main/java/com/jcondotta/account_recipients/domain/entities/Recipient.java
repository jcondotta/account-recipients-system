package com.jcondotta.account_recipients.domain.entities;

import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import com.jcondotta.account_recipients.domain.value_objects.Iban;
import com.jcondotta.account_recipients.domain.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.value_objects.RecipientName;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class Recipient {

  public static final String CLOCK_NOT_NULL_MESSAGE = "clock must not be null";

  private final RecipientId recipientId;
  private final BankAccountId bankAccountId;
  private final RecipientName recipientName;
  private final Iban iban;
  private final ZonedDateTime createdAt;

  private ZonedDateTime deletedAt;

  private Recipient(RecipientId recipientId, BankAccountId bankAccountId, RecipientName recipientName, Iban iban, ZonedDateTime createdAt, ZonedDateTime deletedAt) {
    this.recipientId = requireNonNull(recipientId, "recipientId must not be null");
    this.bankAccountId = requireNonNull(bankAccountId, "bankAccountId must not be null");
    this.recipientName = requireNonNull(recipientName, "recipientName must not be null");
    this.iban = requireNonNull(iban, "iban must not be null");
    this.createdAt = requireNonNull(createdAt, "createdAt must not be null");
    this.deletedAt = deletedAt;
  }

  private Recipient(RecipientId recipientId, BankAccountId bankAccountId, RecipientName recipientName, Iban iban, ZonedDateTime createdAt) {
    this(recipientId, bankAccountId, recipientName, iban, createdAt, null);
  }

  static Recipient create(BankAccountId bankAccountId, RecipientName recipientName, Iban iban, Clock clock) {
    requireNonNull(clock, CLOCK_NOT_NULL_MESSAGE);
    return new Recipient(RecipientId.newId(), bankAccountId, recipientName, iban, ZonedDateTime.now(clock));
  }

  public static Recipient restore(RecipientId recipientId, BankAccountId bankAccountId, RecipientName recipientName, Iban iban, ZonedDateTime createdAt) {
    return new Recipient(recipientId, bankAccountId, recipientName, iban, createdAt);
  }

  void delete(Clock clock) {
    requireNonNull(clock, CLOCK_NOT_NULL_MESSAGE);

    if (!isDeleted()) {
      this.deletedAt = ZonedDateTime.now(clock);
    }
  }

  public RecipientId getRecipientId() {
    return recipientId;
  }

  public BankAccountId getBankAccountId() {
    return bankAccountId;
  }

  public RecipientName getRecipientName() {
    return recipientName;
  }

  public Iban getIban() {
    return iban;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public ZonedDateTime getDeletedAt() {
    return deletedAt;
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Recipient that = (Recipient) o;
    return Objects.equals(recipientId, that.recipientId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(recipientId);
  }
}
