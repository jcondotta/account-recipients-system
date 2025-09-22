package com.jcondotta.account_recipients.application.ports.output.repository;

import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

public interface DeleteAccountRecipientRepository {

    void delete(BankAccountId bankAccountId, AccountRecipientId accountRecipientId);
}
