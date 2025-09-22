package com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.time.ZonedDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BankAccountCdo(UUID bankAccountId, AccountStatusCdo status) {

    public static BankAccountCdo of(UUID bankAccountId, AccountStatusCdo status) {
        return new BankAccountCdo(bankAccountId, status);
    }
}