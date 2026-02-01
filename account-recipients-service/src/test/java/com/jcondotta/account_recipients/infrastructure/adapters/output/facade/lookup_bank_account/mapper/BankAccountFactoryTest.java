package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.mapper;

import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import com.jcondotta.account_recipients.domain.bank_account.enums.AccountStatus;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountCdo;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountFactoryTest {

  private final BankAccountFactory factory = new BankAccountFactory();
  private final UUID bankAccountUUID = UUID.randomUUID();

  @ParameterizedTest
  @ValueSource(strings = {"ACTIVE", "PENDING", "CANCELLED"})
  void shouldCreateBankAccount_whenInputIsValid(String accountStatus) {
    BankAccountCdo cdo = new BankAccountCdo(bankAccountUUID, accountStatus);

    BankAccount account = factory.create(cdo);

    assertThat(account.getBankAccountId())
        .extracting(BankAccountId::value)
        .isEqualTo(bankAccountUUID);

    assertThat(account.getAccountStatus())
        .hasToString(accountStatus);
  }

  @Test
  void shouldThrowNullPointerException_whenSourceIsNull() {
    assertThatThrownBy(() -> factory.create(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("source must not be null");
  }

  @Test
  void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
    BankAccountCdo cdo = new BankAccountCdo(null, "ACTIVE");

    assertThatThrownBy(() -> factory.create(cdo))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("bank account id value must not be null");
  }

  @Test
  void shouldThrowNullPointerException_whenStatusIsNull() {
    BankAccountCdo cdo = new BankAccountCdo(bankAccountUUID, null);

    assertThatThrownBy(() -> factory.create(cdo))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("status value must not be null");
  }

  @ParameterizedTest
  @ValueSource(strings = {"INVALID_STATUS", " ", "", "LOCKED", "DELETED"})
  void shouldMapToUnknownAndLogWarning_whenStatusIsUnknownEnumValue(String invalidStatus) {
    BankAccountCdo cdo = new BankAccountCdo(bankAccountUUID, invalidStatus);

    try (LogCaptor logCaptor = LogCaptor.forClass(BankAccountFactory.class)) {
      BankAccount account = factory.create(cdo);

      assertThat(account.getAccountStatus())
          .isEqualTo(AccountStatus.UNKNOWN);

      assertThat(logCaptor.getWarnLogs())
          .anyMatch(msg ->
              msg.equals("Received unknown status value: " + invalidStatus)
          );
    }
  }
}