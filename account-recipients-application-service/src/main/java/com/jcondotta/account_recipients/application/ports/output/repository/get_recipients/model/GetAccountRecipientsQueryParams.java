package com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model;

import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.QueryLimit;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.RecipientNamePrefix;

import static java.util.Objects.requireNonNullElse;

public record GetAccountRecipientsQueryParams(QueryLimit limit, RecipientNamePrefix namePrefix, PaginationCursor cursor) {

    public static final int DEFAULT_LIMIT = 20;

    public GetAccountRecipientsQueryParams {
        limit = requireNonNullElse(limit, QueryLimit.of(DEFAULT_LIMIT));
    }

    public static GetAccountRecipientsQueryParams of(QueryLimit limit, RecipientNamePrefix namePrefix, PaginationCursor cursor) {
        return new GetAccountRecipientsQueryParams(limit, namePrefix, cursor);
    }

    public static GetAccountRecipientsQueryParams of(QueryLimit limit) {
        return GetAccountRecipientsQueryParams.of(limit, null, null);
    }

    public static GetAccountRecipientsQueryParams of(Integer limit) {
        limit = requireNonNullElse(limit, DEFAULT_LIMIT);
        return GetAccountRecipientsQueryParams.of(QueryLimit.of(limit));
    }
}