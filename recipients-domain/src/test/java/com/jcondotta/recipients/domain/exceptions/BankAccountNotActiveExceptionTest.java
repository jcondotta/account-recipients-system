package com.jcondotta.recipients.domain.exceptions;

import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountNotActiveExceptionTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();

  @Test
  void shouldCreateExceptionWithoutCause_whenBankAccountIdIsValid() {
    var bankAccountId = BankAccountId.of(BANK_ACCOUNT_UUID);
    var exception = new BankAccountNotActiveException(bankAccountId);

    assertThat(exception)
        .isInstanceOf(DomainException.class)
        .hasMessage(BankAccountNotActiveException.BANK_ACCOUNT_NOT_ACTIVE_TEMPLATE)
        .satisfies(
            e -> {
              assertThat(e.title()).isEqualTo(BankAccountNotActiveException.BANK_ACCOUNT_NOT_ACTIVE_TITLE);
              assertThat(e.getCause()).isNull();
              assertThat(e.messageCode()).isEqualTo(BankAccountNotActiveException.BANK_ACCOUNT_NOT_ACTIVE_TEMPLATE);
              assertThat(e.args())
                  .hasSize(1)
                  .containsExactly(BANK_ACCOUNT_UUID);
            });
  }

}