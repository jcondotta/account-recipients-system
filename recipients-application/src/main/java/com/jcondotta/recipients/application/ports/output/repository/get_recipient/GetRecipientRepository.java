package com.jcondotta.recipients.application.ports.output.repository.get_recipient;

import com.jcondotta.recipients.domain.entities.Recipient;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;

import java.util.Optional;

public interface GetRecipientRepository {

  Optional<Recipient> getRecipient(BankAccountId bankAccountId, RecipientId recipientId);
}
