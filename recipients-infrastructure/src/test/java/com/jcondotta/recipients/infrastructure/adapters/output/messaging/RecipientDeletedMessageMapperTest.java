package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.recipients.common.factory.ClockTestFactory;
import com.jcondotta.recipients.domain.events.RecipientDeletedEvent;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientDeletedMessageMapperTest {

  private static final BankAccountId BANK_ACCOUNT_ID =
      new BankAccountId(UUID.randomUUID());

  private static final RecipientId RECIPIENT_ID =
      RecipientId.newId();

  private static final Clock FIXED_CLOCK =
      ClockTestFactory.TEST_CLOCK_FIXED;

  private static final ZonedDateTime OCCURRED_AT = ZonedDateTime.now(FIXED_CLOCK);

  private final RecipientDeletedMessageMapper mapper =
      Mappers.getMapper(RecipientDeletedMessageMapper.class);

  @Test
  void shouldMapRecipientDeletedEventToMessageCorrectly() {
    var event =
        new RecipientDeletedEvent(
            RECIPIENT_ID,
            BANK_ACCOUNT_ID,
            OCCURRED_AT
        );

    assertThat(mapper.fromEvent(event))
        .satisfies(
            message ->
                Assertions.assertAll(
                    () -> assertThat(message.recipientId()).isEqualTo(event.recipientId().value().toString()),
                    () -> assertThat(message.bankAccountId()).isEqualTo(event.bankAccountId().value().toString()),
                    () -> assertThat(message.occurredAt()).isEqualTo(OCCURRED_AT)
                ));
  }

  @Test
  void shouldReturnNull_whenEventIsNull() {
    assertThat(mapper.fromEvent(null)).isNull();
  }
}
