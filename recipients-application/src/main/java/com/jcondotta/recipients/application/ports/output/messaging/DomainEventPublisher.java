package com.jcondotta.recipients.application.ports.output.messaging;

import com.jcondotta.recipients.application.usecase.shared.value_objects.IdempotencyKey;

public interface DomainEventPublisher<T> {

  void send(T event, IdempotencyKey idempotencyKey);
}
