package com.jcondotta.account_recipients.domain.bank_account.exceptions;

import com.jcondotta.account_recipients.domain.shared.exceptions.DomainObjectNotFoundException;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountNotFoundExceptionTest {

    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();

    @Test
    void shouldCreateExceptionWithCauseCorrectly_whenBankAccountIdIsValid() {
        var bankAccountId = BankAccountId.of(BANK_ACCOUNT_UUID);
        var rootCause = new RuntimeException("404 simulated");
        var exception = new BankAccountNotFoundException(bankAccountId, rootCause);

        assertThat(exception)
            .isInstanceOf(DomainObjectNotFoundException.class)
            .hasMessage(BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND_TEMPLATE)
            .satisfies(e -> {
                assertThat(e.getTitle())
                    .isEqualTo(BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND_TITLE);

                assertThat(e.getIdentifiers())
                    .hasSize(1)
                    .containsExactly(BANK_ACCOUNT_UUID);

                assertThat(e.getCause()).isSameAs(rootCause);
            });
    }
}