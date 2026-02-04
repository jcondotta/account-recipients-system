package com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.model.response;

import java.util.UUID;

public record RecipientResponse(
    UUID recipientId, UUID bankAccountId, String recipientName, String iban) {

  public static RecipientResponse of(
      UUID recipientId, UUID bankAccountId, String recipientName, String iban) {
    return new RecipientResponse(recipientId, bankAccountId, recipientName, iban);
  }
}
