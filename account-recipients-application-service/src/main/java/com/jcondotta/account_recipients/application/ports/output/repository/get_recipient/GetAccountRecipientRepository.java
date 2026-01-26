package com.jcondotta.account_recipients.application.ports.output.repository.get_recipient;

import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.util.Optional;

public interface GetAccountRecipientRepository {

  Optional<AccountRecipient> getAccountRecipient(BankAccountId bankAccountId, RecipientId recipientId);
}
