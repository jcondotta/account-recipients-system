package com.jcondotta.account_recipients.application.usecase.delete_recipient.model;

import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeleteAccountRecipientCommandTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId ACCOUNT_RECIPIENT_ID = RecipientId.newId();

  @Test
  void shouldCreateCommand_whenAllParamsAreValid() {
    var command = DeleteAccountRecipientCommand.of(BANK_ACCOUNT_ID, ACCOUNT_RECIPIENT_ID);

    assertThat(command)
        .extracting(
            DeleteAccountRecipientCommand::bankAccountId,
            DeleteAccountRecipientCommand::recipientId)
        .containsExactly(BANK_ACCOUNT_ID, ACCOUNT_RECIPIENT_ID);
  }

  @Test
  void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new DeleteAccountRecipientCommand(null, ACCOUNT_RECIPIENT_ID))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("bankAccountId must not be null");
  }

  @Test
  void shouldThrowNullPointerException_whenRecipientIdIsNull() {
    assertThatThrownBy(() -> new DeleteAccountRecipientCommand(BANK_ACCOUNT_ID, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("recipientId must not be null");
  }
}
