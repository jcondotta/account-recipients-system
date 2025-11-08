package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.mapper;

import com.jcondotta.account_recipients.domain.bank_account.enums.AccountStatus;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountStatusMapperTest {

    private final AccountStatusMapper mapper = AccountStatusMapper.INSTANCE;

    @Test
    void shouldThrowNullPointerException_whenStatusIsNull() {
        assertThatThrownBy(() -> mapper.mapToAccountStatus(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("status value must not be null");
    }

    @ParameterizedTest
    @ValueSource(strings = { "INVALID_STATUS", " ", "", "LOCKED" })
    void shouldMapToUnknownAndLogWarning_whenStatusIsUnknownEnumValue(String invalidStatus) {
        try (LogCaptor logCaptor = LogCaptor.forClass(AccountStatusMapper.class)) {
            var accountStatus = mapper.mapToAccountStatus(invalidStatus);

            assertThat(accountStatus).isEqualTo(AccountStatus.UNKNOWN);
            assertThat(logCaptor.getWarnLogs())
                .anyMatch(msg -> msg.equals("Received unknown status value: " + invalidStatus));
        }
    }
}