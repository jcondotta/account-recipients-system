package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.recipients.domain.events.RecipientCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipientCreatedMessageMapper {

  @Mapping(target = "recipientId", source = "recipientId.value")
  @Mapping(target = "recipientName", source = "recipientName.value")
  @Mapping(target = "bankAccountId", source = "bankAccountId.value")
  @Mapping(target = "iban", source = "iban.value")
  @Mapping(target = "occurredAt", source = "occurredAt")
  RecipientCreatedMessage fromEvent(RecipientCreatedEvent event);
}
