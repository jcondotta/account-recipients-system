package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.mapper;

import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.AccountStatusCdo;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountCdo;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LookupBankAccountCdoFacadeMapperTest {

    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
    private static final AccountStatusCdo ACTIVE_ACCOUNT_STATUS_CDO = AccountStatusCdo.ACTIVE;

    private final LookupBankAccountCdoFacadeMapper mapper = LookupBankAccountCdoFacadeMapper.INSTANCE;

    @Test
    void shouldMapBankAccountCdoToDomain_whenValidCdo() {
        var bankAccountCdo = BankAccountCdo.of(BANK_ACCOUNT_UUID, ACTIVE_ACCOUNT_STATUS_CDO);
        assertThat(mapper.map(bankAccountCdo))
            .satisfies(bankAccount -> {
                assertThat(bankAccount.bankAccountId()).hasToString(bankAccountCdo.bankAccountId().toString());
                assertThat(bankAccount.accountStatus()).hasToString(bankAccountCdo.status().name());
            });
    }

    @Test
    void shouldReturnNull_whenBankAccountCdoIsNull() {
        assertThat(mapper.map(null)).isNull();
    }
}