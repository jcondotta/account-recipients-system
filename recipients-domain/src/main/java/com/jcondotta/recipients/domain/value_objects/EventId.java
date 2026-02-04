package com.jcondotta.recipients.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

public record EventId(UUID value) {

  public static final String ID_NOT_NULL_MESSAGE = "event id value must not be null";

  public EventId {
    Objects.requireNonNull(value, ID_NOT_NULL_MESSAGE);
  }

  public static EventId newEventId() {
    return new EventId(UUID.randomUUID());
  }

  public static EventId of(UUID uuid) {
    return new EventId(uuid);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
