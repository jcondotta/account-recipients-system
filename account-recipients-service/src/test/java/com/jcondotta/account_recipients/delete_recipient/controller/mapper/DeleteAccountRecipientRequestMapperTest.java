package com.jcondotta.account_recipients.delete_recipient.controller.mapper;

import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeleteAccountRecipientRequestMapperTest {

    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
    private static final UUID ACCOUNT_RECIPIENT_UUID = UUID.randomUUID();

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);
    private static final AccountRecipientId ACCOUNT_RECIPIENT_ID = AccountRecipientId.of(ACCOUNT_RECIPIENT_UUID);

    private final DeleteAccountRecipientRequestMapper mapper = DeleteAccountRecipientRequestMapper.INSTANCE;

    @Test
    void shouldMapToCommand_whenBothIdsAreValid() {
        var command = mapper.toCommand(BANK_ACCOUNT_UUID, ACCOUNT_RECIPIENT_UUID);

        assertThat(command)
            .satisfies(it -> {
                assertThat(it.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
                assertThat(it.accountRecipientId()).isEqualTo(ACCOUNT_RECIPIENT_ID);
            });
    }

    @Test
    void shouldThrowException_whenBankAccountIdIsNull() {
        assertThatThrownBy(() -> mapper.toCommand(null, ACCOUNT_RECIPIENT_UUID))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowException_whenAccountRecipientIdIsNull() {
        assertThatThrownBy(() -> mapper.toCommand(BANK_ACCOUNT_UUID, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowException_whenBothIdsAreNull() {
        assertThat(mapper.toCommand(null, null)).isNull();
    }
}
