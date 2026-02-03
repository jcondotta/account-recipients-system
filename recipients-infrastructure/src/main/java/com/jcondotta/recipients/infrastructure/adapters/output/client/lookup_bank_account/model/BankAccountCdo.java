package com.jcondotta.recipients.infrastructure.adapters.output.client.lookup_bank_account.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BankAccountCdo(UUID bankAccountId, String status) {

  public static BankAccountCdo of(UUID bankAccountId, String status) {
    return new BankAccountCdo(bankAccountId, status);
  }
}
