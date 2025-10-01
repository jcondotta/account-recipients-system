package com.jcondotta.account_recipients.application.ports.output.repository.create_recipient;

import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;

public interface CreateAccountRecipientRepository {

    void create(AccountRecipient accountRecipient);
}
