package com.jcondotta.account_recipients.application.usecase.get_recipients.model.result;

import com.jcondotta.account_recipients.application.usecase.get_recipients.model.AccountRecipientDetails;
import com.jcondotta.account_recipients.application.helper.ClockTestFactory;
import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.jcondotta.account_recipients.application.common.fixtures.AccountRecipientFixtures.JEFFERSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetAccountRecipientsResultTest {

    private static final AccountRecipientId ACCOUNT_RECIPIENT_ID = AccountRecipientId.newId();
    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final RecipientName RECIPIENT_NAME = RecipientName.of(JEFFERSON.getRecipientName());
    private static final Iban IBAN = Iban.of(JEFFERSON.getIban());
    private static final ZonedDateTime CREATED_AT = ZonedDateTime.now(ClockTestFactory.testClockFixed);

    private AccountRecipientDetails accountRecipientDetails;
    private String nextCursor;

    @BeforeEach
    void setUp() {
        accountRecipientDetails = AccountRecipientDetails.of(
            ACCOUNT_RECIPIENT_ID,
            BANK_ACCOUNT_ID,
            RECIPIENT_NAME,
            IBAN,
            CREATED_AT
        );

        nextCursor = "next-cursor-token";
    }

    @Test
    void shouldCreateResult_whenValidArguments() {
        var result = new GetAccountRecipientsResult(List.of(accountRecipientDetails), nextCursor);

        assertThat(result.accountRecipients()).containsExactly(accountRecipientDetails);
        assertThat(result.nextCursor()).isEqualTo(nextCursor);
    }

    @Test
    void shouldThrowNullPointerException_whenAccountRecipientsIsNull() {
        assertThatThrownBy(() -> new GetAccountRecipientsResult(null, nextCursor))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("accountRecipients must not be null");
    }

    @Test
    void shouldAllowNullCursor_whenAccountRecipientsIsValid() {
        var result = new GetAccountRecipientsResult(List.of(accountRecipientDetails), null);

        assertThat(result.accountRecipients()).containsExactly(accountRecipientDetails);
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void shouldCreateResultUsingFactoryMethod_whenValidArguments() {
        var result = GetAccountRecipientsResult.of(List.of(accountRecipientDetails), nextCursor);

        assertThat(result.accountRecipients()).containsExactly(accountRecipientDetails);
        assertThat(result.nextCursor()).isEqualTo(nextCursor);
    }
}
