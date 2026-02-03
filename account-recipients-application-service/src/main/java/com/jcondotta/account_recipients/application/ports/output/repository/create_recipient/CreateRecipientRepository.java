package com.jcondotta.account_recipients.application.ports.output.repository.create_recipient;

import com.jcondotta.account_recipients.domain.entities.Recipient;

public interface CreateRecipientRepository {

  Recipient create(Recipient recipient);
}
