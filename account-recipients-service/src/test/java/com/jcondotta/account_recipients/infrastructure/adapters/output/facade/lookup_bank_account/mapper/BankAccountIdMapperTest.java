package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.mapper;

import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountIdMapperTest {

    private final BankAccountIdMapper mapper = BankAccountIdMapper.INSTANCE;

    private final UUID bankAccountUUID = UUID.randomUUID();

    @Test
    void shouldMapBankAccountId_whenBankAccountUUIDIsValid() {
        assertThat(mapper.mapToBankAccountId(bankAccountUUID))
            .extracting(BankAccountId::value)
            .isEqualTo(bankAccountUUID);
    }

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdValueIsNull() {
        assertThatThrownBy(() -> mapper.mapToBankAccountId(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("bank account id value must not be null");
    }
}