package com.jcondotta.recipients.domain.events;

import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.RecipientId;

public sealed interface RecipientEvent extends DomainEvent
    permits RecipientCreatedEvent, RecipientDeletedEvent {

  RecipientId recipientId();

  BankAccountId bankAccountId();
}