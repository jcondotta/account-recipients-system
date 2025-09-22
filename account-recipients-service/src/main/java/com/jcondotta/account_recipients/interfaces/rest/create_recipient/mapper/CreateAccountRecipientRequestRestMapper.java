package com.jcondotta.account_recipients.interfaces.rest.create_recipient.mapper;

import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateAccountRecipientCommand;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.interfaces.rest.create_recipient.model.CreateAccountRecipientRestRequest;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;

@Mapper(
    componentModel = "spring",
    imports = {
        BankAccountId.class,
        RecipientName.class,
        Iban.class,
        ZonedDateTime.class
    }
)
public interface CreateAccountRecipientRequestRestMapper {

    @Mapping(target = "bankAccountId", expression = "java(BankAccountId.of(bankAccountId))")
    @Mapping(target = "recipientName", expression = "java(RecipientName.of(request.recipientName()))")
    @Mapping(target = "iban", expression = "java(Iban.of(request.iban()))")
    @Mapping(target = "createdAt", expression = "java(ZonedDateTime.now(clock))")
    CreateAccountRecipientCommand toCommand(UUID bankAccountId, CreateAccountRecipientRestRequest request, @Context Clock clock);
}
