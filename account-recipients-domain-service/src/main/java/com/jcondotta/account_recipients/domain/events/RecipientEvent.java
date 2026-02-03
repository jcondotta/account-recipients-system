package com.jcondotta.account_recipients.domain.events;

import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import com.jcondotta.account_recipients.domain.value_objects.RecipientId;

public sealed interface RecipientEvent extends DomainEvent
    permits RecipientCreatedEvent, RecipientDeletedEvent {

  RecipientId recipientId();

  BankAccountId bankAccountId();
}