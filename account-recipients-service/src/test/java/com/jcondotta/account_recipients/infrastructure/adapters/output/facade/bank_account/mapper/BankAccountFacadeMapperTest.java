package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.bank_account.mapper;

import com.jcondotta.account_recipients.domain.enums.AccountStatus;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountCdo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountFacadeMapperTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private final BankAccountFacadeMapper mapper =
      new BankAccountFacadeMapperImpl(new BankAccountFactory());

  @ParameterizedTest
  @EnumSource(
      value = AccountStatus.class,
      names = {"UNKNOWN"},
      mode = EnumSource.Mode.EXCLUDE)
  void shouldMapBankAccountCdoToDomain_whenValidCdo(AccountStatus status) {
    var bankAccountCdo = BankAccountCdo.of(BANK_ACCOUNT_UUID, status.name());
    assertThat(mapper.map(bankAccountCdo))
        .satisfies(
            bankAccount -> {
              assertThat(bankAccount.getBankAccountId().value())
                  .isEqualTo(bankAccountCdo.bankAccountId());
              assertThat(bankAccount.getAccountStatus()).hasToString(bankAccountCdo.status());
            });
  }

  @Test
  void shouldReturnNull_whenBankAccountCdoIsNull() {
    assertThat(mapper.map(null)).isNull();
  }
}
