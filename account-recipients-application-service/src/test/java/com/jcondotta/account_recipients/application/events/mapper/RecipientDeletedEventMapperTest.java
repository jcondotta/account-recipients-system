package com.jcondotta.account_recipients.application.events.mapper;

import com.jcondotta.account_recipients.application.helper.ClockTestFactory;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientDeletedEvent;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.UUID;

import static com.jcondotta.account_recipients.application.common.fixtures.AccountRecipientFixtures.JEFFERSON;
import static org.assertj.core.api.Assertions.assertThat;

class RecipientDeletedEventMapperTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientName RECIPIENT_NAME = RecipientName.of(JEFFERSON.getRecipientName());
  private static final Iban IBAN = Iban.of(JEFFERSON.getIban());
  private static final Clock FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;

  private final RecipientDeletedEventMapper mapper = new RecipientDeletedEventMapperImpl();

  @Test
  void shouldMapAccountRecipientToRecipientDeletedEvent_whenAccountRecipientIsValid() {
    var accountRecipient = AccountRecipient.create(BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, FIXED_CLOCK);

    var deleteClock = Clock.systemDefaultZone();
    accountRecipient.delete(deleteClock);

    RecipientDeletedEvent deletedEvent = mapper.fromAccountRecipient(accountRecipient);

    assertThat(deletedEvent)
        .satisfies(
            recipientDeletedEvent -> {
              assertThat(recipientDeletedEvent.recipientId()).isEqualTo(accountRecipient.getRecipientId());
              assertThat(recipientDeletedEvent.bankAccountId()).isEqualTo(accountRecipient.getBankAccountId());
              assertThat(recipientDeletedEvent.occurredAt()).isEqualTo(accountRecipient.getDeletedAt().toInstant());
              assertThat(recipientDeletedEvent.occurredAtZone()).isEqualTo(accountRecipient.getDeletedAt().getZone());
            });
  }

  @Test
  void shouldReturnNull_whenAccountRecipientIsNull() {
    assertThat(mapper.fromAccountRecipient(null)).isNull();
  }
}
