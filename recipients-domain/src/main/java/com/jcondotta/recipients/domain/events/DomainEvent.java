package com.jcondotta.recipients.domain.events;

import com.jcondotta.recipients.domain.value_objects.EventId;

import java.time.ZonedDateTime;

public interface DomainEvent {
  EventId eventId();
  ZonedDateTime occurredAt();
}