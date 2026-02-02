package com.jcondotta.account_recipients.domain.recipient.events;

import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.shared.events.DomainEvent;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

public sealed interface RecipientEvent extends DomainEvent
    permits RecipientCreatedEvent, RecipientDeletedEvent {

    RecipientId recipientId();
    BankAccountId bankAccountId();
}