package com.jcondotta.account_recipients.domain.bank_account.exceptions;

import com.jcondotta.account_recipients.domain.shared.exceptions.DomainObjectNotFoundException;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

public class BankAccountNotFoundException extends DomainObjectNotFoundException {

    public static final String BANK_ACCOUNT_NOT_FOUND_TEMPLATE = "bankAccount.notFound";
    public static final String BANK_ACCOUNT_NOT_FOUND_TITLE = "Bank account not found";

    public BankAccountNotFoundException(BankAccountId bankAccountId, Throwable cause) {
        super(BANK_ACCOUNT_NOT_FOUND_TEMPLATE, BANK_ACCOUNT_NOT_FOUND_TITLE, cause, bankAccountId.value());
    }
}
