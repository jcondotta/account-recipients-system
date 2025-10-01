package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper;

import com.jcondotta.account_recipients.ClockTestFactory;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountRecipientEntityMapperTest {

    private final AccountRecipientEntityMapper mapper = AccountRecipientEntityMapper.INSTANCE;

    private static final AccountRecipientId ACCOUNT_RECIPIENT_ID = AccountRecipientId.newId();
    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final RecipientName RECIPIENT_NAME = RecipientName.of("Jefferson Condotta");
    private static final Iban IBAN = Iban.of("DE89370400440532013000");
    private static final ZonedDateTime CREATED_AT = ZonedDateTime.now(ClockTestFactory.TEST_CLOCK_FIXED);

    @Test
    void shouldMapDomainToEntity_whenValidAccountRecipient() {
        var accountRecipient = new AccountRecipient(
            ACCOUNT_RECIPIENT_ID,
            BANK_ACCOUNT_ID,
            RECIPIENT_NAME,
            IBAN,
            CREATED_AT
        );

        assertThat(mapper.toEntity(accountRecipient))
            .satisfies(entity -> {
                assertThat(entity.getPartitionKey()).isEqualTo(AccountRecipientEntityKey.partitionKey(BANK_ACCOUNT_ID));
                assertThat(entity.getSortKey()).isEqualTo(AccountRecipientEntityKey.sortKey(ACCOUNT_RECIPIENT_ID));
                assertThat(entity.getAccountRecipientId()).isEqualTo(ACCOUNT_RECIPIENT_ID.value());
                assertThat(entity.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID.value());
                assertThat(entity.getRecipientName()).isEqualTo(RECIPIENT_NAME.value());
                assertThat(entity.getIban()).isEqualTo(IBAN.value());
                assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT.toInstant());
                assertThat(entity.getCreatedAtZoneId()).isEqualTo(CREATED_AT.getZone());
            });
    }

    @Test
    void shouldMapEntityToDomain_whenValidEntity() {
        var entity = new AccountRecipientEntity();
        entity.setPartitionKey(AccountRecipientEntityKey.partitionKey(BANK_ACCOUNT_ID));
        entity.setSortKey(AccountRecipientEntityKey.sortKey(ACCOUNT_RECIPIENT_ID));
        entity.setAccountRecipientId(ACCOUNT_RECIPIENT_ID.value());
        entity.setBankAccountId(BANK_ACCOUNT_ID.value());
        entity.setRecipientName(RECIPIENT_NAME.value());
        entity.setIban(IBAN.value());
        entity.setCreatedAt(CREATED_AT.toInstant());
        entity.setCreatedAtZoneId(CREATED_AT.getZone());

        assertThat(mapper.toDomain(entity))
            .satisfies(domain -> {
                assertThat(domain.accountRecipientId()).isEqualTo(ACCOUNT_RECIPIENT_ID);
                assertThat(domain.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
                assertThat(domain.recipientName()).isEqualTo(RECIPIENT_NAME);
                assertThat(domain.iban()).isEqualTo(IBAN);
                assertThat(domain.createdAt()).isEqualTo(CREATED_AT);
            });
    }

    @Test
    void shouldReturnNull_whenDomainIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void shouldReturnNull_whenEntityIsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }
}