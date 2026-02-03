package com.jcondotta.recipients.infrastructure.adapters.output.repository.entity;

import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;

import java.util.UUID;

public final class RecipientEntityKey {

  private static final String PK_PREFIX = "ACCOUNT_OWNER#";
  private static final String SK_PREFIX = "ACCOUNT_RECIPIENT#";

  private RecipientEntityKey() {
  }

  public static String partitionKey(UUID bankAccountId) {
    return PK_PREFIX + bankAccountId.toString();
  }

  public static String partitionKey(BankAccountId bankAccountId) {
    return partitionKey(bankAccountId.value());
  }

  public static String sortKey(UUID recipientId) {
    return SK_PREFIX + recipientId.toString();
  }

  public static String sortKey(RecipientId recipientId) {
    return sortKey(recipientId.value());
  }

  public static BankAccountId extractBankAccountId(String partitionKey) {
    if (partitionKey == null || !partitionKey.startsWith(PK_PREFIX)) {
      throw new IllegalArgumentException("Invalid partitionKey: " + partitionKey);
    }
    var bankAccountId = partitionKey.replace(PK_PREFIX, "");
    return BankAccountId.of(UUID.fromString(bankAccountId));
  }

  public static RecipientId extractRecipientId(String sortKey) {
    if (sortKey == null || !sortKey.startsWith(SK_PREFIX)) {
      throw new IllegalArgumentException("Invalid sortKey: " + sortKey);
    }
    var recipientId = sortKey.replace(SK_PREFIX, "");
    return RecipientId.of(UUID.fromString(recipientId));
  }
}
