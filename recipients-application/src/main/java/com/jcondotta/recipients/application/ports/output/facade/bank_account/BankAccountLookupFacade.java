package com.jcondotta.recipients.application.ports.output.facade.bank_account;

import com.jcondotta.recipients.domain.entities.BankAccount;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;

public interface BankAccountLookupFacade {

  BankAccount byId(BankAccountId bankAccountId);
}
