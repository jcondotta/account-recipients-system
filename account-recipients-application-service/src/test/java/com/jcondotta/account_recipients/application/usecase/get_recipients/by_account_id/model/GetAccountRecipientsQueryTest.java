package com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model;

import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetAccountRecipientsQueryTest {

    private static final UUID BANK_ACCOUNT_UUID_1 = UUID.fromString("1fcaca1b-92ba-43c1-b45c-bacf92868d31");
    private static final UUID BANK_ACCOUNT_UUID_2 = UUID.fromString("d063f4bd-dd1f-41d0-8f47-0d5b9195bfaa");

    @Test
    void shouldCreateQuery_whenBankAccountIdIsValid() {
        var bankAccountId = BankAccountId.of(BANK_ACCOUNT_UUID_1);

        var query = GetAccountRecipientsQuery.of(bankAccountId);

        assertThat(query)
            .extracting(GetAccountRecipientsQuery::bankAccountId)
            .isEqualTo(bankAccountId);
    }

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
        assertThatThrownBy(() -> GetAccountRecipientsQuery.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("bankAccountId must not be null");
    }

    @Test
    void shouldBeEqual_whenQueriesHaveSameBankAccountId() {
        var bankAccountId = BankAccountId.of(BANK_ACCOUNT_UUID_1);

        var query1 = GetAccountRecipientsQuery.of(bankAccountId);
        var query2 = GetAccountRecipientsQuery.of(bankAccountId);

        assertThat(query1)
            .isEqualTo(query2)
            .hasSameHashCodeAs(query2);
    }

    @Test
    void shouldNotBeEqual_whenQueriesHaveDifferentBankAccountId() {
        var query1 = GetAccountRecipientsQuery.of(BankAccountId.of(BANK_ACCOUNT_UUID_1));
        var query2 = GetAccountRecipientsQuery.of(BankAccountId.of(BANK_ACCOUNT_UUID_2));

        assertThat(query1).isNotEqualTo(query2);
    }
}