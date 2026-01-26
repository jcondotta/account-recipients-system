package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record GetRecipientsLastEvaluatedKey(
    UUID bankAccountId, UUID recipientId, String recipientName) {

  public GetRecipientsLastEvaluatedKey {
    requireNonNull(bankAccountId, "bank account id must not be null");
    requireNonNull(recipientId, "account recipient id must not be null");
    requireNonNull(recipientName, "recipient name must not be null");

    if (recipientName.isBlank()) {
      throw new IllegalArgumentException("recipient name must not be blank");
    }
  }

  public static GetRecipientsLastEvaluatedKey of(
      UUID bankAccountId, UUID recipientId, String recipientName) {
    return new GetRecipientsLastEvaluatedKey(bankAccountId, recipientId, recipientName);
  }
}
