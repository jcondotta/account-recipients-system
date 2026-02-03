package com.jcondotta.recipients.delete_recipient.controller.mapper;

import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeleteRecipientRequestMapperTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final UUID ACCOUNT_RECIPIENT_UUID = UUID.randomUUID();

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);
  private static final RecipientId ACCOUNT_RECIPIENT_ID =
      RecipientId.of(ACCOUNT_RECIPIENT_UUID);

  private final DeleteRecipientRequestMapper mapper =
      DeleteRecipientRequestMapper.INSTANCE;

  @Test
  void shouldMapToCommand_whenBothIdsAreValid() {
    var command = mapper.toCommand(BANK_ACCOUNT_UUID, ACCOUNT_RECIPIENT_UUID);

    assertThat(command)
        .satisfies(
            it -> {
              assertThat(it.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
              assertThat(it.recipientId()).isEqualTo(ACCOUNT_RECIPIENT_ID);
            });
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> mapper.toCommand(null, ACCOUNT_RECIPIENT_UUID))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowException_whenRecipientIdIsNull() {
    assertThatThrownBy(() -> mapper.toCommand(BANK_ACCOUNT_UUID, null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowException_whenBothIdsAreNull() {
    assertThat(mapper.toCommand(null, null)).isNull();
  }
}
