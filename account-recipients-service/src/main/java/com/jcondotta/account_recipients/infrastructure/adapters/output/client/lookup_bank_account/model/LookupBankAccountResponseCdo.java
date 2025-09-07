package com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LookupBankAccountResponseCdo(@JsonProperty("bankAccount") BankAccountCdo bankAccountCdo) {

    public static LookupBankAccountResponseCdo of(BankAccountCdo bankAccountCdo) {
        return new LookupBankAccountResponseCdo(bankAccountCdo);
    }
}