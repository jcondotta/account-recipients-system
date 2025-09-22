package com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.mapper;

import com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model.AccountRecipientDetails;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", imports = { AccountRecipientId.class })
public interface GetAccountRecipientsMapper {

    AccountRecipientDetails toAccountRecipient(AccountRecipient accountRecipient);
}
