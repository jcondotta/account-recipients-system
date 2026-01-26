package com.jcondotta.account_recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.account_recipients.ClockTestFactory;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientCreatedMessageMapperTest {

  private static final BankAccountId BANK_ACCOUNT_ID = new BankAccountId(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();
  private static final RecipientName RECIPIENT_NAME = new RecipientName(AccountRecipientFixtures.JEFFERSON.getRecipientName());
  private static final Iban IBAN = new Iban(AccountRecipientFixtures.JEFFERSON.getRecipientIban());

  private static final Clock FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;
  private static final Instant OCCURRED_AT = Instant.now(FIXED_CLOCK);
  private static final ZoneId OCCURRED_AT_ZONE = FIXED_CLOCK.getZone();

  private final RecipientCreatedMessageMapper mapper = Mappers.getMapper(RecipientCreatedMessageMapper.class);

  @Test
  void shouldMapRecipientCreatedEventToMessageCorrectly() {
    var event = new RecipientCreatedEvent(
        RECIPIENT_ID,
        RECIPIENT_NAME,
        BANK_ACCOUNT_ID,
        IBAN,
        OCCURRED_AT,
        OCCURRED_AT_ZONE
    );

    assertThat(mapper.from(event))
        .satisfies(message -> Assertions.assertAll(
            () -> assertThat(message.recipientId()).isEqualTo(event.recipientId().value().toString()),
            () -> assertThat(message.recipientName()).isEqualTo(event.recipientName().value()),
            () -> assertThat(message.bankAccountId()).isEqualTo(event.bankAccountId().value().toString()),
            () -> assertThat(message.iban()).isEqualTo(event.iban().value()),
            () -> assertThat(message.occurredAt()).isEqualTo(OCCURRED_AT),
            () -> assertThat(message.occurredAtZone()).isEqualTo(OCCURRED_AT_ZONE.getId())
        ));
  }

  @Test
  void shouldReturnNull_whenEventIsNull() {
    assertThat(mapper.from(null)).isNull();
  }
}
