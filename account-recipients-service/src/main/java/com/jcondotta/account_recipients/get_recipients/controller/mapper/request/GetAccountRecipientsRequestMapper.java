package com.jcondotta.account_recipients.get_recipients.controller.mapper.request;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetAccountRecipientsQueryParams;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.QueryLimit;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.get_recipients.controller.model.request.GetAccountRecipientsRestRequestParams;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Mapper(
    componentModel = "spring",
    imports = {
        BankAccountId.class,
    }
)
public interface GetAccountRecipientsRequestMapper {

    @Mapping(target = "bankAccountId", expression = "java(BankAccountId.of(bankAccountId))")
    @Mapping(target = "queryParams", source = "requestParams", qualifiedByName = "toQueryParams")
    GetAccountRecipientsQuery toQuery(UUID bankAccountId, GetAccountRecipientsRestRequestParams requestParams);

    @Named("toQueryParams")
    static GetAccountRecipientsQueryParams toQueryParams(GetAccountRecipientsRestRequestParams requestParams) {
        if (requestParams == null) {
            return null;
        }
        var queryLimit = nonNull(requestParams.limit()) ? QueryLimit.of(requestParams.limit()) : null;
        var paginationCursor = nonNull(requestParams.cursor()) ? PaginationCursor.of(requestParams.cursor()) : null;

        return GetAccountRecipientsQueryParams.of(queryLimit, paginationCursor);
    }
}
