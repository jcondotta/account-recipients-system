package com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account;

import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountResponseCdo;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "bankAccountClient", url = "${client.api.bank-accounts.base-url}")
public interface LookupBankAccountClient {

  @GetMapping("/api/v1/bank-accounts/{bankAccountId}")
  BankAccountResponseCdo findById(@PathVariable UUID bankAccountId);
}
