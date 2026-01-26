package com.jcondotta.account_recipients.application.events.mapper;

import com.jcondotta.account_recipients.application.helper.ClockTestFactory;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;

import static com.jcondotta.account_recipients.application.common.fixtures.AccountRecipientFixtures.JEFFERSON;
import static org.assertj.core.api.Assertions.assertThat;

class RecipientCreatedEventMapperTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientName RECIPIENT_NAME =
      RecipientName.of(JEFFERSON.getRecipientName());

  private static final Iban IBAN = Iban.of(JEFFERSON.getIban());

  private static final Clock FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;

  private final RecipientCreatedEventMapper mapper = new RecipientCreatedEventMapperImpl();

  @Test
  void shouldMapAccountRecipientToRecipientCreatedEvent_whenAccountRecipientIsValid() {
    var accountRecipient = AccountRecipient.create(BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, FIXED_CLOCK);

    RecipientCreatedEvent createdEvent = mapper.fromAccountRecipient(accountRecipient);

    assertThat(createdEvent)
        .satisfies(
            recipientCreatedEvent -> {
              assertThat(recipientCreatedEvent.recipientId()).isEqualTo(accountRecipient.getRecipientId());
              assertThat(recipientCreatedEvent.recipientName()).isEqualTo(accountRecipient.getRecipientName());
              assertThat(recipientCreatedEvent.bankAccountId()).isEqualTo(accountRecipient.getBankAccountId());
              assertThat(recipientCreatedEvent.iban()).isEqualTo(accountRecipient.getIban());
              assertThat(recipientCreatedEvent.occurredAt()).isEqualTo(accountRecipient.getCreatedAt().toInstant());
              assertThat(recipientCreatedEvent.occurredAtZone()).isEqualTo(accountRecipient.getCreatedAt().getZone());
            });
  }

  @Test
  void shouldReturnNull_whenAccountRecipientIsNull() {
    assertThat(mapper.fromAccountRecipient(null)).isNull();
  }
}
