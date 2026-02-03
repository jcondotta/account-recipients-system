package com.jcondotta.account_recipients.application.ports.output.facade.bank_account;

import com.jcondotta.account_recipients.domain.entities.BankAccount;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;

public interface BankAccountLookupFacade {

  BankAccount byId(BankAccountId bankAccountId);
}
