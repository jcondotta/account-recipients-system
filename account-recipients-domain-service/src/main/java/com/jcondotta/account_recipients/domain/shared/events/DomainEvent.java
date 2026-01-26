package com.jcondotta.account_recipients.domain.shared.events;

import java.time.Instant;
import java.time.ZoneId;

public interface DomainEvent {
    Instant occurredAt();

    ZoneId occurredAtZone();
}