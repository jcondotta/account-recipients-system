package com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient;

import com.jcondotta.account_recipients.domain.entities.Recipient;

public interface DeleteRecipientRepository {

  void delete(Recipient recipient);
}
