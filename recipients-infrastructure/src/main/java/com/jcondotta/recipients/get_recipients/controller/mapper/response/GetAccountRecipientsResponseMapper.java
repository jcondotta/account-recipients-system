package com.jcondotta.recipients.get_recipients.controller.mapper.response;

import com.jcondotta.recipients.application.usecase.get_recipients.model.RecipientDetails;
import com.jcondotta.recipients.get_recipients.controller.model.response.RecipientResponse;
import com.jcondotta.recipients.get_recipients.controller.model.response.GetRecipientsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GetAccountRecipientsResponseMapper {

  @Mapping(target = "recipientId", source = "recipientId.value")
  @Mapping(target = "bankAccountId", source = "bankAccountId.value")
  @Mapping(target = "recipientName", source = "recipientName.value")
  @Mapping(target = "iban", source = "iban.value")
  RecipientResponse toAccountRecipientResponse(RecipientDetails details);

  List<RecipientResponse> toAccountRecipientResponses(
      List<RecipientDetails> detailsList);

  default GetRecipientsResponse toResponse(
      List<RecipientDetails> detailsList, String nextCursor) {
    return GetRecipientsResponse.of(toAccountRecipientResponses(detailsList), nextCursor);
  }
}
