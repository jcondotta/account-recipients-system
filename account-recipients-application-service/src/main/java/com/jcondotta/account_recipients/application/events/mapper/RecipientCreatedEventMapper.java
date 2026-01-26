package com.jcondotta.account_recipients.application.events.mapper;

import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RecipientCreatedEventMapper {

  @Mapping(target = "recipientId", source = "recipientId")
  @Mapping(target = "recipientName", source = "recipientName")
  @Mapping(target = "bankAccountId", source = "bankAccountId")
  @Mapping(target = "iban", source = "iban")
  @Mapping(target = "occurredAt", expression = "java(accountRecipient.getCreatedAt().toInstant())")
  @Mapping(target = "occurredAtZone", expression = "java(accountRecipient.getCreatedAt().getZone())")
  RecipientCreatedEvent fromAccountRecipient(AccountRecipient accountRecipient);
}
