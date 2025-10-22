package com.jcondotta.account_recipients.application.usecase.create_recipient.model;

import com.jcondotta.account_recipients.application.helper.ClockTestFactory;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.jcondotta.account_recipients.application.common.fixtures.AccountRecipientFixtures.JEFFERSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateAccountRecipientCommandTest {

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final RecipientName RECIPIENT_NAME = RecipientName.of(JEFFERSON.getRecipientName());
    private static final Iban IBAN = Iban.of(JEFFERSON.getIban());
    private static final ZonedDateTime CREATED_AT = ZonedDateTime.now(ClockTestFactory.testClockFixed);

    @Test
    void shouldCreateCommand_whenAllParamsAreValid() {
        var command = CreateAccountRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT);

        assertThat(command)
            .extracting(
                CreateAccountRecipientCommand::bankAccountId,
                CreateAccountRecipientCommand::recipientName,
                CreateAccountRecipientCommand::iban,
                CreateAccountRecipientCommand::createdAt
            )
            .containsExactly(BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT);
    }

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
        assertThatThrownBy(() -> new CreateAccountRecipientCommand(null, RECIPIENT_NAME, IBAN, CREATED_AT))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("bankAccountId must not be null");
    }

    @Test
    void shouldThrowNullPointerException_whenRecipientNameIsNull() {
        assertThatThrownBy(() -> new CreateAccountRecipientCommand(BANK_ACCOUNT_ID, null, IBAN, CREATED_AT))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("recipientName must not be null");
    }

    @Test
    void shouldThrowNullPointerException_whenIbanIsNull() {
        assertThatThrownBy(() -> new CreateAccountRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_NAME, null, CREATED_AT))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("iban must not be null");
    }

    @Test
    void shouldThrowNullPointerException_whenCreatedAtIsNull() {
        assertThatThrownBy(() -> new CreateAccountRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("createdAt must not be null");
    }
}