package com.jcondotta.account_recipients.application.ports.output.repository.get_recipient;

import com.jcondotta.account_recipients.domain.entities.Recipient;
import com.jcondotta.account_recipients.domain.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;

import java.util.Optional;

public interface GetRecipientRepository {

  Optional<Recipient> getRecipient(BankAccountId bankAccountId, RecipientId recipientId);
}
