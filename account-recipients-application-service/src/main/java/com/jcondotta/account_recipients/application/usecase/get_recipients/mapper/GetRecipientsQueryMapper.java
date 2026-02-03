package com.jcondotta.account_recipients.application.usecase.get_recipients.mapper;

import com.jcondotta.account_recipients.application.usecase.get_recipients.model.RecipientDetails;
import com.jcondotta.account_recipients.domain.entities.Recipient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GetRecipientsQueryMapper {

  RecipientDetails toRecipient(Recipient recipient);
}
