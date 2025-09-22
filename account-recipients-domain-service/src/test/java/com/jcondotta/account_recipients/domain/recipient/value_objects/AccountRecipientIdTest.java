package com.jcondotta.account_recipients.domain.recipient.value_objects;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountRecipientIdTest {

    private static final UUID ACCOUNT_RECIPIENT_UUID_1 = UUID.fromString("ff9cdd7f-9a2a-4b0a-9e53-6e9f1d482d4e");

    @Test
    void shouldCreateAccountRecipientId_whenValueIsValid() {
        var accountRecipientId = AccountRecipientId.of(ACCOUNT_RECIPIENT_UUID_1);

        assertThat(accountRecipientId)
            .isNotNull()
            .extracting(AccountRecipientId::value)
            .isEqualTo(ACCOUNT_RECIPIENT_UUID_1);
    }

    @Test
    void shouldGenerateNewAccountRecipientId_whenCallingNewId() {
        var accountRecipientId = AccountRecipientId.newId();

        assertThat(accountRecipientId)
            .isNotNull()
            .extracting(AccountRecipientId::value)
            .isNotNull();
    }

    @Test
    void shouldBeEqual_whenAccountRecipientIdsHaveSameValue() {
        var accountRecipientId1 = AccountRecipientId.of(ACCOUNT_RECIPIENT_UUID_1);
        var accountRecipientId2 = AccountRecipientId.of(ACCOUNT_RECIPIENT_UUID_1);

        assertThat(accountRecipientId1)
            .isEqualTo(accountRecipientId2)
            .hasSameHashCodeAs(accountRecipientId2);
    }

    @Test
    void shouldNotBeEqual_whenAccountRecipientIdsHaveDifferentValues() {
        var accountRecipientId1 = AccountRecipientId.newId();
        var accountRecipientId2 = AccountRecipientId.newId();

        assertThat(accountRecipientId1)
            .isNotEqualTo(accountRecipientId2);
    }

    @Test
    void shouldReturnStringRepresentation_whenCallingToString() {
        var accountRecipientId = AccountRecipientId.of(ACCOUNT_RECIPIENT_UUID_1);
        assertThat(accountRecipientId.toString())
            .isEqualTo(ACCOUNT_RECIPIENT_UUID_1.toString());
    }

    @Test
    void shouldThrowNullPointerException_whenValueIsNull() {
        assertThatThrownBy(() -> AccountRecipientId.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(AccountRecipientId.ID_NOT_NULL_MESSAGE);
    }
}