package com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model;

import com.jcondotta.account_recipients.application.usecase.helper.ClockTestFactory;
import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountRecipientDetailsTest {

    private static final AccountRecipientId ACCOUNT_RECIPIENT_ID = AccountRecipientId.newId();
    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final RecipientName RECIPIENT_NAME = RecipientName.of("Jefferson Condotta");
    private static final Iban IBAN = Iban.of("DE89370400440532013000");
    private static final ZonedDateTime CREATED_AT = ZonedDateTime.now(ClockTestFactory.testClockFixed);

    @Test
    void shouldCreateAccountRecipientDetails_whenAllFieldsAreValid() {
        AccountRecipientDetails details = AccountRecipientDetails.of(
            ACCOUNT_RECIPIENT_ID,
            BANK_ACCOUNT_ID,
            RECIPIENT_NAME,
            IBAN,
            CREATED_AT
        );

        assertThat(details.accountRecipientId()).isEqualTo(ACCOUNT_RECIPIENT_ID);
        assertThat(details.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
        assertThat(details.recipientName()).isEqualTo(RECIPIENT_NAME);
        assertThat(details.iban()).isEqualTo(IBAN);
        assertThat(details.createdAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void shouldThrowNullPointerException_whenAccountRecipientIdIsNull() {
        assertThatThrownBy(() -> new AccountRecipientDetails(
            null, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("accountRecipientId must not be null");
    }

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
        assertThatThrownBy(() -> new AccountRecipientDetails(
            ACCOUNT_RECIPIENT_ID, null, RECIPIENT_NAME, IBAN, CREATED_AT))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("bankAccountId must not be null");
    }

    @Test
    void shouldThrowNullPointerException_whenRecipientNameIsNull() {
        assertThatThrownBy(() -> new AccountRecipientDetails(
            ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, null, IBAN, CREATED_AT))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("recipientName must not be null");
    }

    @Test
    void shouldThrowNullPointerException_whenIbanIsNull() {
        assertThatThrownBy(() -> new AccountRecipientDetails(
            ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, null, CREATED_AT))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("iban must not be null");
    }

    @Test
    void shouldThrowNullPointerException_whenCreatedAtIsNull() {
        assertThatThrownBy(() -> new AccountRecipientDetails(
            ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("createdAt must not be null");
    }
}