package com.jcondotta.recipients.application.ports.output.repository.create_recipient;

import com.jcondotta.recipients.domain.entities.Recipient;

public interface CreateRecipientRepository {

  Recipient create(Recipient recipient);
}
