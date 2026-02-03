package com.jcondotta.account_recipients.application.ports.output.messaging;

import com.jcondotta.account_recipients.domain.events.RecipientCreatedEvent;

public interface RecipientCreatedEventPublisher extends RecipientEventPublisher<RecipientCreatedEvent> {

}
