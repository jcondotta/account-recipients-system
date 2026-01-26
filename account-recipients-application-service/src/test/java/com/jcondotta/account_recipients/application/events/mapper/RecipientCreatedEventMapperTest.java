package com.jcondotta.account_recipients.application.events.mapper;

import com.jcondotta.account_recipients.application.helper.ClockTestFactory;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.jcondotta.account_recipients.application.common.fixtures.AccountRecipientFixtures.JEFFERSON;
import static org.assertj.core.api.Assertions.assertThat;

class RecipientCreatedEventMapperTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientName RECIPIENT_NAME =
      RecipientName.of(JEFFERSON.getRecipientName());

  private static final Iban IBAN = Iban.of(JEFFERSON.getIban());
  private static final ZonedDateTime CREATED_AT =
      ZonedDateTime.now(ClockTestFactory.testClockFixed);

//  private final RecipientCreatedEventMapper mapper = RecipientCreatedEventMapper.INSTANCE;

//  @Test
//  void shouldMapAccountRecipientToRecipientCreatedEvent_whenAccountRecipientIsValid() {
//    var accountRecipient = AccountRecipient.of(BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT);
//
//    RecipientCreatedEvent createdEvent = mapper.fromAccountRecipient(accountRecipient);
//
//    assertThat(createdEvent)
//        .satisfies(
//            recipientCreatedEvent -> {
//              assertThat(recipientCreatedEvent.recipientId())
//                  .isEqualTo(accountRecipient.recipientId());
//              assertThat(recipientCreatedEvent.recipientName())
//                  .isEqualTo(accountRecipient.recipientName());
//              assertThat(recipientCreatedEvent.bankAccountId())
//                  .isEqualTo(accountRecipient.bankAccountId());
//              assertThat(recipientCreatedEvent.iban()).isEqualTo(accountRecipient.iban());
//              assertThat(recipientCreatedEvent.occurredAt())
//                  .isEqualTo(accountRecipient.createdAt().toInstant());
//              assertThat(recipientCreatedEvent.occurredAtZone())
//                  .isEqualTo(accountRecipient.createdAt().getZone());
//            });
//  }

//  @Test
//  void shouldReturnNull_whenAccountRecipientIsNull() {
//    assertThat(mapper.fromAccountRecipient(null)).isNull();
//  }
}
