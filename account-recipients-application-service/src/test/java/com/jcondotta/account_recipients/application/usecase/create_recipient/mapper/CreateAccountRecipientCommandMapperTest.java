package com.jcondotta.account_recipients.application.usecase.create_recipient.mapper;

import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateAccountRecipientCommand;
import com.jcondotta.account_recipients.application.helper.ClockTestFactory;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.jcondotta.account_recipients.application.common.fixtures.AccountRecipientFixtures.JEFFERSON;
import static org.assertj.core.api.Assertions.assertThat;

class CreateAccountRecipientCommandMapperTest {

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final RecipientName RECIPIENT_NAME = RecipientName.of(JEFFERSON.getRecipientName());
    private static final Iban IBAN = Iban.of(JEFFERSON.getIban());
    private static final ZonedDateTime CREATED_AT = ZonedDateTime.now(ClockTestFactory.testClockFixed);

    private final CreateAccountRecipientCommandMapper mapper = CreateAccountRecipientCommandMapper.INSTANCE;

    @Test
    void shouldMapCommandToAccountRecipient_whenCommandIsValid() {
        var command = CreateAccountRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT);

        assertThat(mapper.toAccountRecipient(command))
            .satisfies(accountRecipient -> {
                assertThat(accountRecipient.accountRecipientId()).isNotNull();
                assertThat(accountRecipient.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
                assertThat(accountRecipient.recipientName()).isEqualTo(RECIPIENT_NAME);
                assertThat(accountRecipient.iban()).isEqualTo(IBAN);
                assertThat(accountRecipient.createdAt()).isEqualTo(CREATED_AT);
            });
    }

    @Test
    void shouldReturnNull_whenCommandIsNull() {
        assertThat(mapper.toAccountRecipient(null)).isNull();
    }
}