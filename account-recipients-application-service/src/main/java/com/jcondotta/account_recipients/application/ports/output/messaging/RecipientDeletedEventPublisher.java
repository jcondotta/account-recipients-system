package com.jcondotta.account_recipients.application.ports.output.messaging;

import com.jcondotta.account_recipients.domain.recipient.events.RecipientDeletedEvent;

public interface RecipientDeletedEventPublisher {

  void send(RecipientDeletedEvent event);
}
