package com.jcondotta.account_recipients.application.events.mapper;

import com.jcondotta.account_recipients.application.helper.ClockTestFactory;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.jcondotta.account_recipients.application.common.fixtures.AccountRecipientFixtures.JEFFERSON;

class RecipientDeletedEventMapperTest {

  private static final BankAccountId BANK_ACCOUNT_ID =
      BankAccountId.of(UUID.randomUUID());

  private static final RecipientName RECIPIENT_NAME =
      RecipientName.of(JEFFERSON.getRecipientName());

  private static final Iban IBAN =
      Iban.of(JEFFERSON.getIban());

  private static final ZonedDateTime DELETED_AT =
      ZonedDateTime.now(ClockTestFactory.testClockFixed);

  private final RecipientDeletedEventMapper mapper =
      RecipientDeletedEventMapper.INSTANCE;

// @Test
// void shouldMapAccountRecipientToRecipientDeletedEvent_whenAccountRecipientIsValid() {
//  var accountRecipient = AccountRecipient.of(BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, DELETED_AT.minusDays(1));
//
//  accountRecipient.delete(DELETED_AT);
//
//  RecipientDeletedEvent deletedEvent =
//      mapper.fromAccountRecipient(accountRecipient);
//
////  assertThat(deletedEvent)
////      .satisfies(
////          recipientDeletedEvent -> {
////           assertThat(recipientDeletedEvent.recipientId())
////               .isEqualTo(accountRecipient.recipientId());
////           assertThat(recipientDeletedEvent.bankAccountId())
////               .isEqualTo(accountRecipient.bankAccountId());
////           assertThat(recipientDeletedEvent.occurredAt())
////               .isEqualTo(accountRecipient.().toInstant());
////           assertThat(recipientDeletedEvent.occurredAtZone())
////               .isEqualTo(accountRecipient.deletedAt().getZone());
////          });
// }
//
// @Test
// void shouldReturnNull_whenAccountRecipientIsNull() {
//  assertThat(mapper.fromAccountRecipient(null)).isNull();
// }
}
