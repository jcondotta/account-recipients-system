package com.jcondotta.account_recipients.get_recipients.controller.model.response;

import java.util.UUID;

public record AccountRecipientResponse(
    UUID recipientId, UUID bankAccountId, String recipientName, String iban) {

  public static AccountRecipientResponse of(
      UUID recipientId, UUID bankAccountId, String recipientName, String iban) {
    return new AccountRecipientResponse(recipientId, bankAccountId, recipientName, iban);
  }
}
