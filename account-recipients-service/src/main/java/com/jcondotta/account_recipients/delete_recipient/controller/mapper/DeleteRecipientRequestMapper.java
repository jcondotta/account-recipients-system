package com.jcondotta.account_recipients.delete_recipient.controller.mapper;

import com.jcondotta.account_recipients.application.usecase.delete_recipient.model.DeleteRecipientCommand;
import com.jcondotta.account_recipients.domain.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
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
