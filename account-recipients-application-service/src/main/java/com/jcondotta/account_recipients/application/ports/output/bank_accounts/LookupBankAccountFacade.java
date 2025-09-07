package com.jcondotta.account_recipients.application.ports.output.bank_accounts;

import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface LookupBankAccountFacade {

    BankAccount byId(BankAccountId bankAccountId);

    default CompletableFuture<BankAccount> byIdAsync(BankAccountId bankAccountId, Executor executor) {
        return CompletableFuture.supplyAsync(() -> this.byId(bankAccountId), executor);
    }
}