package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper;

import com.jcondotta.account_recipients.common.factory.ClockTestFactory;
import com.jcondotta.account_recipients.domain.entities.Recipient;
import com.jcondotta.account_recipients.domain.value_objects.Iban;
import com.jcondotta.account_recipients.domain.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountRecipientEntityMapperTest {

  private static final RecipientId ACCOUNT_RECIPIENT_ID = RecipientId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientName RECIPIENT_NAME = RecipientName.of("Jefferson Condotta");
  private static final Iban IBAN = Iban.of("DE89370400440532013000");
  private static final ZonedDateTime CREATED_AT =
      ZonedDateTime.now(ClockTestFactory.TEST_CLOCK_FIXED);
  private final AccountRecipientEntityMapper mapper = AccountRecipientEntityMapper.INSTANCE;

  @Test
  void shouldMapDomainToEntity_whenValidAccountRecipient() {
    var recipient = Recipient.restore(ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT);

    assertThat(mapper.toEntity(recipient))
        .satisfies(
            entity -> {
              assertThat(entity.getPartitionKey())
                  .isEqualTo(AccountRecipientEntityKey.partitionKey(BANK_ACCOUNT_ID));
              assertThat(entity.getSortKey())
                  .isEqualTo(AccountRecipientEntityKey.sortKey(ACCOUNT_RECIPIENT_ID));
              assertThat(entity.getRecipientId()).isEqualTo(ACCOUNT_RECIPIENT_ID.value());
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
    entity.setRecipientId(ACCOUNT_RECIPIENT_ID.value());
    entity.setBankAccountId(BANK_ACCOUNT_ID.value());
    entity.setRecipientName(RECIPIENT_NAME.value());
    entity.setIban(IBAN.value());
    entity.setCreatedAt(CREATED_AT.toInstant());
    entity.setCreatedAtZoneId(CREATED_AT.getZone());

    assertThat(mapper.toDomain(entity))
        .satisfies(
            domain -> {
              assertThat(domain.getRecipientId()).isEqualTo(ACCOUNT_RECIPIENT_ID);
              assertThat(domain.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
              assertThat(domain.getRecipientName()).isEqualTo(RECIPIENT_NAME);
              assertThat(domain.getIban()).isEqualTo(IBAN);
              assertThat(domain.getCreatedAt()).isEqualTo(CREATED_AT);
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
