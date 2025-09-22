package com.jcondotta.account_recipients.domain.recipient.exceptions;

import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.shared.exceptions.DomainObjectNotFoundException;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

public class AccountRecipientNotFoundException extends DomainObjectNotFoundException {

    public static final String ACCOUNT_RECIPIENT_NOT_FOUND_TEMPLATE = "accountRecipient.notFound";
    public static final String ACCOUNT_RECIPIENT_NOT_FOUND_TITLE = "Account recipient not found";

    public AccountRecipientNotFoundException(BankAccountId bankAccountId, AccountRecipientId accountRecipientId) {
        super(ACCOUNT_RECIPIENT_NOT_FOUND_TEMPLATE, ACCOUNT_RECIPIENT_NOT_FOUND_TITLE, bankAccountId.value(), accountRecipientId.value());
    }

    public AccountRecipientNotFoundException(BankAccountId bankAccountId, AccountRecipientId accountRecipientId, Throwable cause) {
        super(ACCOUNT_RECIPIENT_NOT_FOUND_TEMPLATE, ACCOUNT_RECIPIENT_NOT_FOUND_TITLE, cause, bankAccountId.value(), accountRecipientId.value());
    }
}
