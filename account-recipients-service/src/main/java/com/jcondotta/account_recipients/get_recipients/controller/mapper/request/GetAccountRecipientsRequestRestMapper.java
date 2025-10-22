package com.jcondotta.account_recipients.get_recipients.controller.mapper.request;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetAccountRecipientsQueryParams;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.RecipientNamePrefix;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.QueryLimit;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.get_recipients.controller.model.request.GetAccountRecipientsRestRequestParams;
import org.mapstruct.*;

import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Mapper(
    componentModel = "spring",
    imports = {
        BankAccountId.class,
    }
)
public interface GetAccountRecipientsRequestRestMapper {

    @Mapping(target = "bankAccountId", source = "bankAccountId", qualifiedByName = "mapBankAccountId")
    @Mapping(target = "queryParams", source = "requestParams", qualifiedByName = "toQueryParams")
    GetAccountRecipientsQuery toQuery(UUID bankAccountId, GetAccountRecipientsRestRequestParams requestParams);

    @Named("mapBankAccountId")
    default BankAccountId mapBankAccountId(UUID bankAccountId) {
        requireNonNull(bankAccountId, "bankAccountId must not be null");
        return BankAccountId.of(bankAccountId);
    }

    @Named("toQueryParams")
    default GetAccountRecipientsQueryParams toQueryParams(GetAccountRecipientsRestRequestParams requestParams){
        requireNonNull(requestParams, "requestParams must not be null");

        return new GetAccountRecipientsQueryParams(
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
