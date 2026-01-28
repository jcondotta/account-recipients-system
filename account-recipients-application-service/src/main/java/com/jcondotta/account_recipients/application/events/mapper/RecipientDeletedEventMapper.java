package com.jcondotta.account_recipients.application.events.mapper;

import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientDeletedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipientDeletedEventMapper {

  @Mapping(target = "recipientId", source = "recipientId")
  @Mapping(target = "bankAccountId", source = "bankAccountId")
  @Mapping(target = "occurredAt", source = "deletedAt")
  RecipientDeletedEvent fromAccountRecipient(AccountRecipient accountRecipient);
}
