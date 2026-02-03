package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper;

import com.jcondotta.account_recipients.domain.entities.Recipient;
import com.jcondotta.account_recipients.domain.value_objects.Iban;
import com.jcondotta.account_recipients.domain.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
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

  default AccountRecipientEntity toEntity(Recipient recipient) {
    if (Objects.isNull(recipient)) {
      return null;
    }

    return new AccountRecipientEntity(
        recipient.getRecipientId().value(),
        recipient.getBankAccountId().value(),
        recipient.getRecipientName().value(),
        recipient.getIban().value(),
        recipient.getCreatedAt());
  }

  default Recipient toDomain(AccountRecipientEntity entity) {
    if (entity == null) {
      return null;
    }

    return Recipient.restore(
            RecipientId.of(entity.getRecipientId()),
            BankAccountId.of(entity.getBankAccountId()),
            RecipientName.of(entity.getRecipientName()),
            Iban.of(entity.getIban()),
            ZonedDateTime.ofInstant(entity.getCreatedAt(), entity.getCreatedAtZoneId())
    );
  }
}
