package com.jcondotta.account_recipients.delete_recipient.controller.mapper;

import com.jcondotta.account_recipients.application.usecase.delete_recipient.model.DeleteAccountRecipientCommand;
import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(
    componentModel = "spring",
    imports = {
        BankAccountId.class,
        AccountRecipientId.class,
    }
)
public interface DeleteAccountRecipientRequestMapper {

    DeleteAccountRecipientRequestMapper INSTANCE = Mappers.getMapper(DeleteAccountRecipientRequestMapper.class);

    @Mapping(target = "bankAccountId", expression = "java(BankAccountId.of(bankAccountId))")
    @Mapping(target = "accountRecipientId", expression = "java(AccountRecipientId.of(accountRecipientId))")
    DeleteAccountRecipientCommand toCommand(UUID bankAccountId, UUID accountRecipientId);
}
