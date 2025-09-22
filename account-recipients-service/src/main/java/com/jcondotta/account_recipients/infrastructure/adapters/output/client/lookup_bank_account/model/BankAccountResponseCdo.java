package com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BankAccountResponseCdo(@JsonProperty("bankAccount") BankAccountCdo bankAccountCdo) {

    public static BankAccountResponseCdo of(BankAccountCdo bankAccountCdo) {
        return new BankAccountResponseCdo(bankAccountCdo);
    }
}