package com.jcondotta.recipients.application.ports.output.repository.delete_recipient;

import com.jcondotta.recipients.domain.entities.Recipient;

public interface DeleteRecipientRepository {

  void delete(Recipient recipient);
}
