package com.jcondotta.account_recipients.create_recipient.controller.model;

import jakarta.validation.constraints.NotBlank;

public record CreateRecipientRestRequest(@NotBlank String recipientName, @NotBlank String iban) {

  public static CreateRecipientRestRequest of(String recipientName, String iban) {
    return new CreateRecipientRestRequest(recipientName, iban);
  }
}
