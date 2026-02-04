package com.jcondotta.recipients.common.factory;

import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntity;
import org.iban4j.Iban;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;

public class RecipientEntityTestFactory {

  public static RecipientEntity create(UUID recipientId, UUID bankAccountId, String recipientName, String iban, Clock clock) {
    return new RecipientEntity(recipientId, bankAccountId, recipientName, iban, ZonedDateTime.now(clock));
  }

  public static RecipientEntity create(UUID bankAccountId, String recipientName) {
    return create(UUID.randomUUID(), bankAccountId, recipientName, Iban.random().toString(), ClockTestFactory.TEST_CLOCK_FIXED);
  }
}
