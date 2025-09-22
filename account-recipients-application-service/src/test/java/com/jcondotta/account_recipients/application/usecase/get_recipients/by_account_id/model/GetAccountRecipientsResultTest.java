package com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model;

import com.jcondotta.account_recipients.application.usecase.helper.ClockTestFactory;
import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetAccountRecipientsResultTest {

    private static final AccountRecipientId ACCOUNT_RECIPIENT_ID = AccountRecipientId.newId();
    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final RecipientName RECIPIENT_NAME = RecipientName.of("Jefferson Condotta");
    private static final Iban IBAN = Iban.of("DE89370400440532013000");
    private static final ZonedDateTime CREATED_AT = ZonedDateTime.now(ClockTestFactory.testClockFixed);

    @Test
    void shouldCreateResult_whenAccountRecipientsListIsValid() {
        var accountRecipientDetails = AccountRecipientDetails.of(
            ACCOUNT_RECIPIENT_ID,
            BANK_ACCOUNT_ID,
            RECIPIENT_NAME,
            IBAN,
            CREATED_AT
        );

        var recipients = List.of(accountRecipientDetails);
        GetAccountRecipientsResult accountRecipientsResult = GetAccountRecipientsResult.of(recipients);

        assertThat(accountRecipientsResult.accountRecipients())
            .containsExactly(accountRecipientDetails);
    }

    @Test
    void shouldThrowNullPointerException_whenAccountRecipientsListIsNull() {
        assertThatThrownBy(() -> GetAccountRecipientsResult.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("accountRecipients must not be null");
    }
}