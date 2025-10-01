package com.jcondotta.account_recipients.application.usecase.get_recipients.mapper;

import com.jcondotta.account_recipients.application.usecase.get_recipients.model.AccountRecipientDetails;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GetAccountRecipientsQueryMapper {

    GetAccountRecipientsQueryMapper INSTANCE = Mappers.getMapper(GetAccountRecipientsQueryMapper.class);

    AccountRecipientDetails toAccountRecipient(AccountRecipient accountRecipient);
}
