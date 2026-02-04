package com.jcondotta.recipients.infrastructure.adapters.input.rest.create_recipient.model;

import jakarta.validation.constraints.NotBlank;

public record CreateRecipientRestRequest(@NotBlank String recipientName, @NotBlank String iban) {

  public static CreateRecipientRestRequest of(String recipientName, String iban) {
    return new CreateRecipientRestRequest(recipientName, iban);
  }
}
