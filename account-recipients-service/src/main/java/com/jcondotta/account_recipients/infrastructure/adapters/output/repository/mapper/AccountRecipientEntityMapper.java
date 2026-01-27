package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper;

import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.ZonedDateTime;
import java.util.Objects;

@Mapper(
    componentModel = "spring",
    builder = @Builder(disableBuilder = true),
    imports = {
        AccountRecipientEntityKey.class,
        RecipientId.class,
        BankAccountId.class,
        RecipientName.class,
        Iban.class,
    })
public interface AccountRecipientEntityMapper {

  AccountRecipientEntityMapper INSTANCE = Mappers.getMapper(AccountRecipientEntityMapper.class);

  default AccountRecipientEntity toEntity(AccountRecipient accountRecipient) {
    if (Objects.isNull(accountRecipient)) {
      return null;
    }

    return new AccountRecipientEntity(
        accountRecipient.getRecipientId().value(),
        accountRecipient.getBankAccountId().value(),
        accountRecipient.getRecipientName().value(),
        accountRecipient.getIban().value(),
        accountRecipient.getCreatedAt());
  }

  default AccountRecipient toDomain(AccountRecipientEntity entity) {
    if (entity == null) {
      return null;
    }

    return AccountRecipient.restore(
            RecipientId.of(entity.getRecipientId()),
            BankAccountId.of(entity.getBankAccountId()),
            RecipientName.of(entity.getRecipientName()),
            Iban.of(entity.getIban()),
            ZonedDateTime.ofInstant(entity.getCreatedAt(), entity.getCreatedAtZoneId())
    );
  }
}
