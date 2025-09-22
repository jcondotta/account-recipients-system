package com.jcondotta.account_recipients.application.ports.output.facade.lookup_bank_account;

import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

public interface LookupBankAccountFacade {

    BankAccount byId(BankAccountId bankAccountId);

}