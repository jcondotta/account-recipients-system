package com.jcondotta.recipients.application.usecase.get_recipients.model;

import com.jcondotta.recipients.application.helper.ClockTestFactory;
import com.jcondotta.recipients.domain.value_objects.Iban;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.RecipientName;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.jcondotta.recipients.application.common.fixtures.RecipientFixtures.JEFFERSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientDetailsTest {

  private static final RecipientId ACCOUNT_RECIPIENT_ID = RecipientId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientName RECIPIENT_NAME =
      RecipientName.of(JEFFERSON.getRecipientName());
  private static final Iban IBAN = Iban.of(JEFFERSON.getIban());
  private static final ZonedDateTime CREATED_AT =
      ZonedDateTime.now(ClockTestFactory.TEST_CLOCK_FIXED);

  @Test
  void shouldCreateAccountRecipientDetails_whenAllFieldsAreValid() {
    RecipientDetails details =
        RecipientDetails.of(
            ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT);

    assertThat(details.recipientId()).isEqualTo(ACCOUNT_RECIPIENT_ID);
    assertThat(details.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(details.recipientName()).isEqualTo(RECIPIENT_NAME);
    assertThat(details.iban()).isEqualTo(IBAN);
    assertThat(details.createdAt()).isEqualTo(CREATED_AT);
  }

  @Test
  void shouldThrowNullPointerException_whenRecipientIdIsNull() {
    assertThatThrownBy(
        () ->
            new RecipientDetails(
                null, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("recipientId must not be null");
  }

  @Test
  void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
    assertThatThrownBy(
        () ->
            new RecipientDetails(
                ACCOUNT_RECIPIENT_ID, null, RECIPIENT_NAME, IBAN, CREATED_AT))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("bankAccountId must not be null");
  }

  @Test
  void shouldThrowNullPointerException_whenRecipientNameIsNull() {
    assertThatThrownBy(
        () ->
            new RecipientDetails(
                ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, null, IBAN, CREATED_AT))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("recipientName must not be null");
  }

  @Test
  void shouldThrowNullPointerException_whenIbanIsNull() {
    assertThatThrownBy(
        () ->
            new RecipientDetails(
                ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, null, CREATED_AT))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("iban must not be null");
  }

  @Test
  void shouldThrowNullPointerException_whenCreatedAtIsNull() {
    assertThatThrownBy(
        () ->
            new RecipientDetails(
                ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("createdAt must not be null");
  }
}
