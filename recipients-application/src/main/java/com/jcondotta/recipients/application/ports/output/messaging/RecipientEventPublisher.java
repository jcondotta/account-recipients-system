package com.jcondotta.recipients.application.ports.output.messaging;

import com.jcondotta.recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.recipients.domain.events.RecipientEvent;

public interface RecipientEventPublisher<T extends RecipientEvent> extends DomainEventPublisher<T> {

  void send(T event, IdempotencyKey idempotencyKey);
}
