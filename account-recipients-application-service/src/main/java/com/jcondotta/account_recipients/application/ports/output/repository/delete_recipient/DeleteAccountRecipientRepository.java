package com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient;

import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;

public interface DeleteAccountRecipientRepository {

  void delete(AccountRecipient accountRecipient);
}
