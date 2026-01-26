package com.jcondotta.account_recipients.application.ports.output.messaging;

import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientCreatedEvent;

public interface RecipientCreatedEventPublisher {

  void send(RecipientCreatedEvent event, IdempotencyKey idempotencyKey);
}
