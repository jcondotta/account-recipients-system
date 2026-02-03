package com.jcondotta.recipients.get_recipients.controller.mapper.request;

import com.jcondotta.recipients.application.ports.output.repository.get_recipients.model.GetRecipientsQueryParams;
import com.jcondotta.recipients.application.ports.output.repository.shared.value_objects.PaginationCursor;
import com.jcondotta.recipients.application.ports.output.repository.shared.value_objects.QueryLimit;
import com.jcondotta.recipients.application.ports.output.repository.shared.value_objects.RecipientNamePrefix;
import com.jcondotta.recipients.application.usecase.get_recipients.model.query.GetRecipientsQuery;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.get_recipients.controller.model.request.GetRecipientsRestRequestParams;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Mapper(
    componentModel = "spring",
    imports = {
        BankAccountId.class,
    })
public interface GetRecipientsRequestRestMapper {

  @Mapping(target = "bankAccountId", source = "bankAccountId", qualifiedByName = "mapBankAccountId")
  @Mapping(target = "queryParams", source = "requestParams", qualifiedByName = "toQueryParams")
  GetRecipientsQuery toQuery(
      UUID bankAccountId, GetRecipientsRestRequestParams requestParams);

  @Named("mapBankAccountId")
  default BankAccountId mapBankAccountId(UUID bankAccountId) {
    requireNonNull(bankAccountId, "bankAccountId must not be null");
    return BankAccountId.of(bankAccountId);
  }

  @Named("toQueryParams")
  default GetRecipientsQueryParams toQueryParams(
      GetRecipientsRestRequestParams requestParams) {
    requireNonNull(requestParams, "requestParams must not be null");

    return new GetRecipientsQueryParams(
        mapQueryLimit(requestParams.limit()),
        mapNamePrefix(requestParams.namePrefix()),
        mapPaginationCursor(requestParams.cursor()));
  }

  @Named("mapQueryLimit")
  default QueryLimit mapQueryLimit(Integer limit) {
    return limit == null ? null : QueryLimit.of(limit);
  }

  @Named("mapPaginationCursor")
  default PaginationCursor mapPaginationCursor(String cursor) {
    return cursor == null || cursor.isBlank() ? null : new PaginationCursor(cursor);
  }

  @Named("mapNamePrefix")
  default RecipientNamePrefix mapNamePrefix(String namePrefix) {
    return namePrefix == null || namePrefix.isBlank() ? null : new RecipientNamePrefix(namePrefix);
  }
}
