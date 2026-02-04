package com.jcondotta.recipients.infrastructure.adapters.input.rest.create_recipient.mapper;

import com.jcondotta.recipients.application.usecase.create_recipient.model.CreateRecipientCommand;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.Iban;
import com.jcondotta.recipients.domain.value_objects.RecipientName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(
    componentModel = "spring",
    imports = {BankAccountId.class, RecipientName.class, Iban.class})
public interface CreateRecipientRequestRestMapper {

  @Mapping(target = "bankAccountId", expression = "java(BankAccountId.of(bankAccountId))")
  @Mapping(target = "recipientName", expression = "java(RecipientName.of(request.recipientName()))")
  @Mapping(target = "iban", expression = "java(Iban.of(request.iban()))")
  CreateRecipientCommand toCommand(UUID bankAccountId, CreateRecipientRestRequest request);
}
