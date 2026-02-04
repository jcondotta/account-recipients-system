package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class EventMetadataFactory {

  public EventMetadata create() {

    return new EventMetadata(Instant.now());
  }
}

