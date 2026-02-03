package com.jcondotta.recipients.application.usecase.get_recipients.model.result;

import com.jcondotta.recipients.application.helper.ClockTestFactory;
import com.jcondotta.recipients.application.usecase.get_recipients.model.RecipientDetails;
import com.jcondotta.recipients.domain.value_objects.Iban;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.RecipientName;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.jcondotta.recipients.application.common.fixtures.RecipientFixtures.JEFFERSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetRecipientsResultTest {

  private static final RecipientId ACCOUNT_RECIPIENT_ID = RecipientId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientName RECIPIENT_NAME =
      RecipientName.of(JEFFERSON.getRecipientName());
  private static final Iban IBAN = Iban.of(JEFFERSON.getIban());
  private static final ZonedDateTime CREATED_AT =
      ZonedDateTime.now(ClockTestFactory.TEST_CLOCK_FIXED);

  private RecipientDetails recipientDetails;
  private String nextCursor;

  @BeforeEach
  void setUp() {
    recipientDetails =
        RecipientDetails.of(
            ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT);

    nextCursor = "next-cursor-token";
  }

  @Test
  void shouldCreateResult_whenValidArguments() {
    var result = new GetRecipientsResult(List.of(recipientDetails), nextCursor);

    assertThat(result.recipients()).containsExactly(recipientDetails);
    assertThat(result.nextCursor()).isEqualTo(nextCursor);
  }

  @Test
  void shouldThrowNullPointerException_whenRecipientsIsNull() {
    assertThatThrownBy(() -> new GetRecipientsResult(null, nextCursor))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("recipients must not be null");
  }

  @Test
  void shouldAllowNullCursor_whenRecipientsIsValid() {
    var result = new GetRecipientsResult(List.of(recipientDetails), null);

    assertThat(result.recipients()).containsExactly(recipientDetails);
    assertThat(result.nextCursor()).isNull();
  }

  @Test
  void shouldCreateResultUsingFactoryMethod_whenValidArguments() {
    var result = GetRecipientsResult.of(List.of(recipientDetails), nextCursor);

    assertThat(result.recipients()).containsExactly(recipientDetails);
    assertThat(result.nextCursor()).isEqualTo(nextCursor);
  }
}
