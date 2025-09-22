package com.jcondotta.account_recipients.application.usecase.create_recipient.mapper;

import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateAccountRecipientCommand;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", imports = { AccountRecipientId.class })
public interface CreateAccountRecipientCommandMapper {

    CreateAccountRecipientCommandMapper INSTANCE = Mappers.getMapper(CreateAccountRecipientCommandMapper.class);

    @Mapping(target = "accountRecipientId", expression = "java(AccountRecipientId.newId())")
    AccountRecipient toAccountRecipient(CreateAccountRecipientCommand command);
}
