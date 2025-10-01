package com.jcondotta.account_recipients.domain.recipient.exceptions;

import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.shared.exceptions.DomainObjectNotFoundException;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountRecipientNotFoundExceptionTest {

    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
    private static final UUID ACCOUNT_RECIPIENT_UUID = UUID.randomUUID();

    @Test
    void shouldCreateExceptionWithCauseCorrectly_whenParamsAreValid() {
        var bankAccountId = BankAccountId.of(BANK_ACCOUNT_UUID);
        var accountRecipientId = AccountRecipientId.of(ACCOUNT_RECIPIENT_UUID);
        var rootCause = new RuntimeException("404 simulated");

        var exception = new AccountRecipientNotFoundException(bankAccountId, accountRecipientId, rootCause);

        assertThat(exception)
            .isInstanceOf(DomainObjectNotFoundException.class)
            .hasMessage(AccountRecipientNotFoundException.ACCOUNT_RECIPIENT_NOT_FOUND_TEMPLATE)
            .satisfies(e -> {
                assertThat(e.getTitle())
                    .isEqualTo(AccountRecipientNotFoundException.ACCOUNT_RECIPIENT_NOT_FOUND_TITLE);

                assertThat(e.getIdentifiers())
                    .hasSize(2)
                    .containsExactly(BANK_ACCOUNT_UUID, ACCOUNT_RECIPIENT_UUID);

                assertThat(e.getCause())
                    .isSameAs(rootCause);
            });
    }
}