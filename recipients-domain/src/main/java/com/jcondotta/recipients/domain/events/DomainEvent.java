package com.jcondotta.recipients.domain.events;

import java.time.ZonedDateTime;

public interface DomainEvent {
  ZonedDateTime occurredAt();
}