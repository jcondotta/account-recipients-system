package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.recipients.domain.events.RecipientDeletedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipientDeletedMessageMapper {

  @Mapping(target = "recipientId", source = "recipientId.value")
  @Mapping(target = "bankAccountId", source = "bankAccountId.value")
  @Mapping(target = "occurredAt", source = "occurredAt")
  RecipientDeletedMessage fromEvent(RecipientDeletedEvent event);
}
