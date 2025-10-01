package com.jcondotta.account_recipients.application.usecase.get_recipients.mapper;

import com.jcondotta.account_recipients.application.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.application.helper.ClockTestFactory;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.jcondotta.account_recipients.application.common.fixtures.AccountRecipientFixtures.JEFFERSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class GetAccountRecipientsQueryMapperTest {

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final RecipientName RECIPIENT_NAME = RecipientName.of(JEFFERSON.getRecipientName());
    private static final Iban IBAN = Iban.of(JEFFERSON.getIban());
    private static final ZonedDateTime CREATED_AT = ZonedDateTime.now(ClockTestFactory.testClockFixed);
    private static final AccountRecipientId ACCOUNT_RECIPIENT_ID = AccountRecipientId.newId();

    private final GetAccountRecipientsQueryMapper mapper = GetAccountRecipientsQueryMapper.INSTANCE;

    @Test
    void shouldMapToAccountRecipientDetails_whenValidAccountRecipientDomainObject() {
        var accountRecipient = new AccountRecipient(
            ACCOUNT_RECIPIENT_ID,
            BANK_ACCOUNT_ID,
            RECIPIENT_NAME,
            IBAN,
            CREATED_AT
        );

        assertThat(mapper.toAccountRecipient(accountRecipient))
            .satisfies(details -> assertAll(
                () -> assertThat(details.accountRecipientId()).isEqualTo(accountRecipient.accountRecipientId()),
                () -> assertThat(details.bankAccountId()).isEqualTo(accountRecipient.bankAccountId()),
                () -> assertThat(details.recipientName()).isEqualTo(accountRecipient.recipientName()),
                () -> assertThat(details.iban()).isEqualTo(accountRecipient.iban()),
                () -> assertThat(details.createdAt()).isEqualTo(accountRecipient.createdAt())
            ));
    }

    @Test
    void shouldReturnNull_whenAccountRecipientIsNull() {
        assertThat(mapper.toAccountRecipient(null)).isNull();
    }
}