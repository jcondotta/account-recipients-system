package com.jcondotta.recipients.infrastructure.adapters.output.repository.mapper;

import com.jcondotta.recipients.domain.entities.Recipient;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.Iban;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.RecipientName;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntity;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntityKey;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.ZonedDateTime;
import java.util.Objects;

@Mapper(
    componentModel = "spring",
    builder = @Builder(disableBuilder = true),
    imports = {
        RecipientEntityKey.class,
        RecipientId.class,
        BankAccountId.class,
        RecipientName.class,
        Iban.class,
    })
public interface RecipientEntityMapper {

  RecipientEntityMapper INSTANCE = Mappers.getMapper(RecipientEntityMapper.class);

  default RecipientEntity toEntity(Recipient recipient) {
    if (Objects.isNull(recipient)) {
      return null;
    }

    return new RecipientEntity(
        recipient.getRecipientId().value(),
        recipient.getBankAccountId().value(),
        recipient.getRecipientName().value(),
        recipient.getIban().value(),
        recipient.getCreatedAt());
  }

  default Recipient toDomain(RecipientEntity entity) {
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
