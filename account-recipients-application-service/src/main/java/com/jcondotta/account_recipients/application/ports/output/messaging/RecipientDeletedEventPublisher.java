package com.jcondotta.account_recipients.application.ports.output.messaging;

import com.jcondotta.account_recipients.domain.events.RecipientDeletedEvent;

public interface RecipientDeletedEventPublisher extends RecipientEventPublisher<RecipientDeletedEvent> {

}
