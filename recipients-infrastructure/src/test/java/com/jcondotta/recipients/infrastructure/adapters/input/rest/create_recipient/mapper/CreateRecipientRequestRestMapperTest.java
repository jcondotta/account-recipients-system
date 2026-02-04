package com.jcondotta.recipients.infrastructure.adapters.input.rest.create_recipient.mapper;

import com.jcondotta.recipients.application.usecase.create_recipient.model.CreateRecipientCommand;
import com.jcondotta.recipients.common.fixtures.RecipientFixtures;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.Iban;
import com.jcondotta.recipients.domain.value_objects.RecipientName;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link CreateRecipientRequestRestMapper}.
 */
class CreateRecipientRequestRestMapperTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);

  private static final String RECIPIENT_NAME_STRING = RecipientFixtures.JEFFERSON.getRecipientName();
  private static final RecipientName RECIPIENT_NAME = RecipientName.of(RECIPIENT_NAME_STRING);

  private static final String VALID_IBAN = RecipientFixtures.JEFFERSON.getRecipientIban();
  private static final Iban IBAN = Iban.of(VALID_IBAN);

  private final CreateRecipientRequestRestMapper mapper = new CreateRecipientRequestRestMapperImpl();

  @Test
  void shouldMapToCommand_whenRequestIsValid() {
    var request = new CreateRecipientRestRequest(RECIPIENT_NAME_STRING, VALID_IBAN);

    CreateRecipientCommand command = mapper.toCommand(BANK_ACCOUNT_UUID, request);

    assertThat(command).satisfies(
        it -> {
          assertThat(it.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
          assertThat(it.recipientName()).isEqualTo(RECIPIENT_NAME);
          assertThat(it.iban()).isEqualTo(IBAN);
        });
  }

  @Test
  void shouldReturnNull_whenBankAccountIdAndRequestAreNull() {
    var result = mapper.toCommand(null, null);
    assertThat(result).isNull();
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNullButRequestIsNot() {
    var request = new CreateRecipientRestRequest(RECIPIENT_NAME_STRING, VALID_IBAN);

    assertThatThrownBy(() -> mapper.toCommand(null, request))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowException_whenRequestIsNullButBankAccountIdIsNot() {
    assertThatThrownBy(() -> mapper.toCommand(BANK_ACCOUNT_UUID, null))
        .isInstanceOf(NullPointerException.class);
  }
}
