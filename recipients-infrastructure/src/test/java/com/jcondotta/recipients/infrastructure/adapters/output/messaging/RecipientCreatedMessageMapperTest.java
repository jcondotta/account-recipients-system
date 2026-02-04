package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.recipients.common.factory.ClockTestFactory;
import com.jcondotta.recipients.common.fixtures.RecipientFixtures;
import com.jcondotta.recipients.domain.events.RecipientCreatedEvent;
import com.jcondotta.recipients.domain.value_objects.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientCreatedMessageMapperTest {

  private static final EventId EVENT_ID = EventId.newEventId();
  private static final BankAccountId BANK_ACCOUNT_ID = new BankAccountId(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();
  private static final RecipientName RECIPIENT_NAME = new RecipientName(RecipientFixtures.JEFFERSON.getRecipientName());
  private static final Iban IBAN = new Iban(RecipientFixtures.JEFFERSON.getRecipientIban());

  private static final Clock FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;
  private static final ZonedDateTime OCCURRED_AT = ZonedDateTime.now(FIXED_CLOCK);

  private final RecipientCreatedMessageMapper mapper = Mappers.getMapper(RecipientCreatedMessageMapper.class);

  @Test
  void shouldMapRecipientCreatedEventToMessageCorrectly() {
    var event = new RecipientCreatedEvent(
        EVENT_ID,
        RECIPIENT_ID,
        RECIPIENT_NAME,
        BANK_ACCOUNT_ID,
        IBAN,
        OCCURRED_AT
    );

    assertThat(mapper.fromEvent(event))
        .satisfies(message -> Assertions.assertAll(
            () -> assertThat(message.eventId()).isEqualTo(event.eventId().value()),
            () -> assertThat(message.recipientId()).isEqualTo(event.recipientId().value()),
            () -> assertThat(message.recipientName()).isEqualTo(event.recipientName().value()),
            () -> assertThat(message.bankAccountId()).isEqualTo(event.bankAccountId().value()),
            () -> assertThat(message.iban()).isEqualTo(event.iban().value()),
            () -> assertThat(message.occurredAt()).isEqualTo(OCCURRED_AT)
        ));
  }

  @Test
  void shouldReturnNull_whenEventIsNull() {
    assertThat(mapper.fromEvent(null)).isNull();
  }
}
