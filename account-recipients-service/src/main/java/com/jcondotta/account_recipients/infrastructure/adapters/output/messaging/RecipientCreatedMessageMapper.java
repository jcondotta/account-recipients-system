package com.jcondotta.account_recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.account_recipients.domain.recipient.events.RecipientCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipientCreatedMessageMapper {

  @Mapping(target = "recipientId", source = "recipientId.value")
  @Mapping(target = "recipientName", source = "recipientName.value")
  @Mapping(target = "bankAccountId", source = "bankAccountId.value")
  @Mapping(target = "iban", source = "iban.value")
  @Mapping(target = "occurredAt", source = "occurredAt")
  RecipientCreatedMessage from(RecipientCreatedEvent event);
}
