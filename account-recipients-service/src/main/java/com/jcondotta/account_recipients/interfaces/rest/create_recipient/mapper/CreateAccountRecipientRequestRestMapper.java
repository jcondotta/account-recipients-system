package com.jcondotta.account_recipients.interfaces.rest.create_recipient.mapper;

import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import com.jcondotta.account_recipients.domain.value_objects.RecipientIban;
import com.jcondotta.account_recipients.domain.value_objects.RecipientName;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    imports = {
        BankAccountId.class,
        RecipientName.class,
        RecipientIban.class,
    }
)
public interface CreateAccountRecipientRequestRestMapper {

//    @Mapping(target = "bankAccountId", expression = "java(BankAccountId.of(request.bankAccountId()))")
//    @Mapping(target = "recipientName", expression = "java(RecipientName.of(request.recipientName()))")
//    @Mapping(target = "recipientIban", expression = "java(RecipientIban.of(request.recipientIban()))")
//    CreateAccountRecipientCommand toCommand(CreateAccountRecipientRestRequest request);
}
