package com.jcondotta.recipients.infrastructure.adapters.input.rest.delete_recipient.mapper;

import com.jcondotta.recipients.application.usecase.delete_recipient.model.DeleteRecipientCommand;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(
    componentModel = "spring",
    imports = {
        BankAccountId.class,
        RecipientId.class,
    })
public interface DeleteRecipientRequestMapper {

  DeleteRecipientRequestMapper INSTANCE =
      Mappers.getMapper(DeleteRecipientRequestMapper.class);

  @Mapping(target = "bankAccountId", expression = "java(BankAccountId.of(bankAccountId))")
  @Mapping(target = "recipientId", expression = "java(RecipientId.of(recipientId))")
  DeleteRecipientCommand toCommand(UUID bankAccountId, UUID recipientId);
}
