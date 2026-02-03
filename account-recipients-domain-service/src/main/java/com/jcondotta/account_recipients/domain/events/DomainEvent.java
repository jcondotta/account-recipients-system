package com.jcondotta.account_recipients.domain.events;

import java.time.ZonedDateTime;

public interface DomainEvent {
  ZonedDateTime occurredAt();
}