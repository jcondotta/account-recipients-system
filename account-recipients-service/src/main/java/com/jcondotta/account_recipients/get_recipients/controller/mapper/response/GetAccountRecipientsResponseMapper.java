package com.jcondotta.account_recipients.get_recipients.controller.mapper.response;

import com.jcondotta.account_recipients.application.usecase.get_recipients.model.RecipientDetails;
import com.jcondotta.account_recipients.get_recipients.controller.model.response.AccountRecipientResponse;
import com.jcondotta.account_recipients.get_recipients.controller.model.response.GetAccountRecipientsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GetAccountRecipientsResponseMapper {

  @Mapping(target = "recipientId", source = "recipientId.value")
  @Mapping(target = "bankAccountId", source = "bankAccountId.value")
  @Mapping(target = "recipientName", source = "recipientName.value")
  @Mapping(target = "iban", source = "iban.value")
  AccountRecipientResponse toAccountRecipientResponse(RecipientDetails details);

  List<AccountRecipientResponse> toAccountRecipientResponses(
      List<RecipientDetails> detailsList);

  default GetAccountRecipientsResponse toResponse(
      List<RecipientDetails> detailsList, String nextCursor) {
    return GetAccountRecipientsResponse.of(toAccountRecipientResponses(detailsList), nextCursor);
  }
}
