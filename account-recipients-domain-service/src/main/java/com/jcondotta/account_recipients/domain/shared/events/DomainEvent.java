package com.jcondotta.account_recipients.domain.shared.events;

import java.time.ZonedDateTime;

public interface DomainEvent {
    ZonedDateTime occurredAt();
}