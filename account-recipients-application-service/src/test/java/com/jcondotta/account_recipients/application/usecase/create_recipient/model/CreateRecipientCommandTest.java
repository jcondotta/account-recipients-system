package com.jcondotta.account_recipients.application.usecase.create_recipient.model;

import com.jcondotta.account_recipients.domain.value_objects.Iban;
import com.jcondotta.account_recipients.domain.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.jcondotta.account_recipients.application.common.fixtures.RecipientFixtures.JEFFERSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateRecipientCommandTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientName RECIPIENT_NAME = RecipientName.of(JEFFERSON.getRecipientName());
  private static final Iban IBAN = Iban.of(JEFFERSON.getIban());

  @Test
  void shouldCreateCommand_whenAllParamsAreValid() {
    var command = CreateRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN);

    assertThat(command)
        .extracting(
            CreateRecipientCommand::bankAccountId,
            CreateRecipientCommand::recipientName,
            CreateRecipientCommand::iban)
        .containsExactly(BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN);
  }

  @Test
  void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
    assertThatThrownBy(
        () -> new CreateRecipientCommand(null, RECIPIENT_NAME, IBAN))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("bankAccountId must not be null");
  }

  @Test
  void shouldThrowNullPointerException_whenRecipientNameIsNull() {
    assertThatThrownBy(
        () -> new CreateRecipientCommand(BANK_ACCOUNT_ID, null, IBAN))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("recipientName must not be null");
  }

  @Test
  void shouldThrowNullPointerException_whenIbanIsNull() {
    assertThatThrownBy(
        () ->
            new CreateRecipientCommand(
                BANK_ACCOUNT_ID, RECIPIENT_NAME, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("iban must not be null");
  }
}
