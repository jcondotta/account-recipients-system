package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.mapper;

import com.jcondotta.account_recipients.domain.bank_account.enums.AccountStatus;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountCdo;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class LookupBankAccountCdoFacadeMapperTest {

    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();

    private final LookupBankAccountCdoFacadeMapper mapper = Mappers.getMapper(LookupBankAccountCdoFacadeMapper.class);

    @ParameterizedTest
    @EnumSource(value = AccountStatus.class, names = { "UNKNOWN" }, mode = EnumSource.Mode.EXCLUDE)
    void shouldMapBankAccountCdoToDomain_whenValidCdo(AccountStatus status) {
        var bankAccountCdo = BankAccountCdo.of(BANK_ACCOUNT_UUID, status.name());
        assertThat(mapper.map(bankAccountCdo))
            .satisfies(bankAccount -> {
                assertThat(bankAccount.bankAccountId()).hasToString(bankAccountCdo.bankAccountId().toString());
                assertThat(bankAccount.accountStatus()).hasToString(bankAccountCdo.status());
            });
    }

    @Test
    void shouldThrowIllegalArgumentException_whenBankAccountIdIsNull() {
        var bankAccountCdo = BankAccountCdo.of(null, "ACTIVE");

        assertThatThrownBy(() -> mapper.map(bankAccountCdo))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Missing required field: bankAccountId");
    }

    @Test
    void shouldThrowIllegalArgumentException_whenStatusIsNull() {
        var bankAccountCdo = BankAccountCdo.of(BANK_ACCOUNT_UUID, null);

        assertThatThrownBy(() -> mapper.map(bankAccountCdo))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Missing required field: status");
    }

    @ParameterizedTest
    @ValueSource(strings = { "INVALID_STATUS", " ", "", "LOCKED" })
    void shouldLogWarning_whenAccountStatusIsUnknownEnumValue(String invalidStatus) {
        try (LogCaptor logCaptor = LogCaptor.forClass(LookupBankAccountCdoFacadeMapper.class)) {
            var bankAccountCdo = BankAccountCdo.of(BANK_ACCOUNT_UUID, invalidStatus);
            var bankAccount = mapper.map(bankAccountCdo);

            assertThat(bankAccount.accountStatus()).isEqualTo(AccountStatus.UNKNOWN);
            assertThat(logCaptor.getWarnLogs())
                .anyMatch(msg -> msg.equals("Received unknown status value: " + invalidStatus));
        }
    }

    @Test
    void shouldReturnNull_whenBankAccountCdoIsNull() {
        assertThat(mapper.map(null)).isNull();
    }
}