package com.jcondotta.account_recipients.domain.bank_account.exceptions;

import com.jcondotta.account_recipients.domain.shared.exceptions.DomainObjectNotFoundException;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

public class BankAccountNotFoundException extends DomainObjectNotFoundException {

    public static final String BANK_ACCOUNT_BY_ACCOUNT_ID_NOT_FOUND_TEMPLATE = "bankAccount.byAccountId.notFound";

    public BankAccountNotFoundException(BankAccountId bankAccountId) {
        super(BANK_ACCOUNT_BY_ACCOUNT_ID_NOT_FOUND_TEMPLATE, bankAccountId.value());
    }
}
