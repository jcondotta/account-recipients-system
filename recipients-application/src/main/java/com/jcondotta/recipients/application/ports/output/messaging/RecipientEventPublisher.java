package com.jcondotta.recipients.application.ports.output.messaging;

import com.jcondotta.recipients.domain.events.RecipientEvent;

public interface RecipientEventPublisher<T extends RecipientEvent> extends DomainEventPublisher<T> {

}
