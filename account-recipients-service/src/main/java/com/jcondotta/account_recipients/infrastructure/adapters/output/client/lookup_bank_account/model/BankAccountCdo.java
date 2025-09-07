package com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BankAccountCdo(UUID bankAccountId, AccountStatusCdo accountStatus) {

    public static BankAccountCdo of(UUID bankAccountId, AccountStatusCdo accountStatus) {
        return new BankAccountCdo(bankAccountId, accountStatus);
    }
}