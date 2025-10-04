package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity;

import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class AccountRecipientEntityKeyTest {

    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
    private static final UUID RECIPIENT_UUID = UUID.randomUUID();

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);
    private static final AccountRecipientId ACCOUNT_RECIPIENT_ID = AccountRecipientId.of(RECIPIENT_UUID);

    @Test
    void shouldBuildPartitionKey_fromUuid() {
        var pk = AccountRecipientEntityKey.partitionKey(BANK_ACCOUNT_UUID);

        assertThat(pk).isEqualTo("ACCOUNT_OWNER#" + BANK_ACCOUNT_UUID);
    }

    @Test
    void shouldBuildPartitionKey_fromBankAccountId() {
        var pk = AccountRecipientEntityKey.partitionKey(BANK_ACCOUNT_ID);

        assertThat(pk).isEqualTo("ACCOUNT_OWNER#" + BANK_ACCOUNT_UUID);
    }

    @Test
    void shouldBuildSortKey_fromUuid() {
        var sk = AccountRecipientEntityKey.sortKey(RECIPIENT_UUID);

        assertThat(sk).isEqualTo("ACCOUNT_RECIPIENT#" + RECIPIENT_UUID);
    }

    @Test
    void shouldBuildSortKey_fromAccountRecipientId() {
        var sk = AccountRecipientEntityKey.sortKey(ACCOUNT_RECIPIENT_ID);

        assertThat(sk).isEqualTo("ACCOUNT_RECIPIENT#" + RECIPIENT_UUID);
    }

    @Test
    void shouldExtractBankAccountId_fromValidPartitionKey() {
        var pk = "ACCOUNT_OWNER#" + BANK_ACCOUNT_UUID;

        var result = AccountRecipientEntityKey.extractBankAccountId(pk);

        assertThat(result).isEqualTo(BANK_ACCOUNT_ID);
    }

    @Test
    void shouldExtractAccountRecipientId_fromValidSortKey() {
        var sk = "ACCOUNT_RECIPIENT#" + RECIPIENT_UUID;

        var result = AccountRecipientEntityKey.extractAccountRecipientId(sk);

        assertThat(result).isEqualTo(ACCOUNT_RECIPIENT_ID);
    }

    @Test
    void shouldThrowException_whenPartitionKeyIsNull() {
        assertThatThrownBy(() -> AccountRecipientEntityKey.extractBankAccountId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid partitionKey: null");
    }

    @Test
    void shouldThrowException_whenPartitionKeyHasInvalidPrefix() {
        var invalidPk = "WRONG#" + BANK_ACCOUNT_UUID;

        assertThatThrownBy(() -> AccountRecipientEntityKey.extractBankAccountId(invalidPk))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid partitionKey");
    }

    @Test
    void shouldThrowException_whenSortKeyIsNull() {
        assertThatThrownBy(() -> AccountRecipientEntityKey.extractAccountRecipientId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid sortKey: null");
    }

    @Test
    void shouldThrowException_whenSortKeyHasInvalidPrefix() {
        var invalidSk = "WRONG#" + RECIPIENT_UUID;

        assertThatThrownBy(() -> AccountRecipientEntityKey.extractAccountRecipientId(invalidSk))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid sortKey");
    }

    @Test
    void shouldThrowException_whenPartitionKeyHasInvalidUUID() {
        var invalidPk = "ACCOUNT_OWNER#not-a-uuid";

        assertThatThrownBy(() -> AccountRecipientEntityKey.extractBankAccountId(invalidPk))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid UUID string");
    }

    @Test
    void shouldThrowException_whenSortKeyHasInvalidUUID() {
        var invalidSk = "ACCOUNT_RECIPIENT#1234";

        assertThatThrownBy(() -> AccountRecipientEntityKey.extractAccountRecipientId(invalidSk))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid UUID string");
    }
}