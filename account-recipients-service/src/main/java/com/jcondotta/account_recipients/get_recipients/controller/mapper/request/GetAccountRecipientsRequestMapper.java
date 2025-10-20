package com.jcondotta.account_recipients.get_recipients.controller.mapper.request;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetAccountRecipientsQueryParams;
import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.RecipientNamePrefix;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.QueryLimit;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.get_recipients.controller.model.request.GetAccountRecipientsRestRequestParams;
import org.mapstruct.*;

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
    @Mapping(target = "limit", source = "limit", qualifiedByName = "mapQueryLimit")
    @Mapping(target = "cursor", source = "cursor", qualifiedByName = "mapPaginationCursor")
    @Mapping(target = "namePrefix", source = "namePrefix", qualifiedByName = "mapNamePrefix")
    GetAccountRecipientsQueryParams toQueryParams(GetAccountRecipientsRestRequestParams requestParams);

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
