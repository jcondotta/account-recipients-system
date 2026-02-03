package com.jcondotta.account_recipients.application.usecase.get_recipients.mapper;

import com.jcondotta.account_recipients.application.helper.ClockTestFactory;
import com.jcondotta.account_recipients.domain.entities.Recipient;
import com.jcondotta.account_recipients.domain.value_objects.Iban;
import com.jcondotta.account_recipients.domain.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.jcondotta.account_recipients.application.common.fixtures.RecipientFixtures.JEFFERSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class GetRecipientsQueryMapperTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientName RECIPIENT_NAME = RecipientName.of(JEFFERSON.getRecipientName());
  private static final Iban IBAN = Iban.of(JEFFERSON.getIban());
  private static final RecipientId ACCOUNT_RECIPIENT_ID = RecipientId.newId();
  private static final ZonedDateTime CREATED_AT = ZonedDateTime.now(ClockTestFactory.TEST_CLOCK_FIXED);

  private final GetRecipientsQueryMapper mapper = new GetRecipientsQueryMapperImpl();

  @Test
  void shouldMapToAccountRecipientDetails_whenValidRecipientDomainObject() {
    var recipient = Recipient.restore(ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT);

    assertThat(mapper.toRecipient(recipient))
        .satisfies(
            details ->
                assertAll(
                    () -> assertThat(details.recipientId()).isEqualTo(recipient.getRecipientId()),
                    () -> assertThat(details.bankAccountId()).isEqualTo(recipient.getBankAccountId()),
                    () -> assertThat(details.recipientName()).isEqualTo(recipient.getRecipientName()),
                    () -> assertThat(details.iban()).isEqualTo(recipient.getIban()),
                    () -> assertThat(details.createdAt()).isEqualTo(recipient.getCreatedAt())));
  }

  @Test
  void shouldReturnNull_whenAccountRecipientIsNull() {
    assertThat(mapper.toRecipient(null)).isNull();
  }
}
