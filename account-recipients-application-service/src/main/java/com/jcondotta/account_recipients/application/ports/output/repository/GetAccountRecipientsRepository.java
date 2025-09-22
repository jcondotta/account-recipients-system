package com.jcondotta.account_recipients.application.ports.output.repository;

import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.util.List;

public interface GetAccountRecipientsRepository {

    List<AccountRecipient> byBankAccountId(BankAccountId bankAccountId);
}
