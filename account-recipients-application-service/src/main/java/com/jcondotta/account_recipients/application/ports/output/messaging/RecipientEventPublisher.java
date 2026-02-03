package com.jcondotta.account_recipients.application.ports.output.messaging;

import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.domain.events.RecipientCreatedEvent;
import com.jcondotta.account_recipients.domain.events.RecipientEvent;

public interface RecipientEventPublisher<T extends RecipientEvent> extends DomainEventPublisher<T> {

  void send(T event, IdempotencyKey idempotencyKey);
}
