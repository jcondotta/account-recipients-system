package com.jcondotta.account_recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.account_recipients.ClockTestFactory;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientDeletedEvent;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientDeletedMessageMapperTest {

  private static final BankAccountId BANK_ACCOUNT_ID =
      new BankAccountId(UUID.randomUUID());

  private static final RecipientId RECIPIENT_ID =
      RecipientId.newId();

  private static final Clock FIXED_CLOCK =
      ClockTestFactory.TEST_CLOCK_FIXED;

  private static final Instant OCCURRED_AT =
      Instant.now(FIXED_CLOCK);

  private static final ZoneId OCCURRED_AT_ZONE =
      FIXED_CLOCK.getZone();

  private final RecipientDeletedMessageMapper mapper =
      Mappers.getMapper(RecipientDeletedMessageMapper.class);

  @Test
  void shouldMapRecipientDeletedEventToMessageCorrectly() {
    var event =
        new RecipientDeletedEvent(
            RECIPIENT_ID,
            BANK_ACCOUNT_ID,
            OCCURRED_AT,
            OCCURRED_AT_ZONE
        );

    assertThat(mapper.from(event))
        .satisfies(
            message ->
                Assertions.assertAll(
                    () -> assertThat(message.recipientId()).isEqualTo(event.recipientId().value().toString()),
                    () -> assertThat(message.bankAccountId()).isEqualTo(event.bankAccountId().value().toString()),
                    () -> assertThat(message.occurredAt()).isEqualTo(OCCURRED_AT),
                    () -> assertThat(message.occurredAtZone()).isEqualTo(OCCURRED_AT_ZONE.getId())
                ));
  }

  @Test
  void shouldReturnNull_whenEventIsNull() {
    assertThat(mapper.from(null)).isNull();
  }
}
