package com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model;

import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.QueryLimit;

import static java.util.Objects.requireNonNullElse;

public record GetAccountRecipientsQueryParams(QueryLimit limit, PaginationCursor cursor) {

    public static final int DEFAULT_LIMIT = 10;

    public GetAccountRecipientsQueryParams {
        limit = requireNonNullElse(limit, QueryLimit.of(DEFAULT_LIMIT));
    }

    public static GetAccountRecipientsQueryParams of(QueryLimit limit, PaginationCursor cursor) {
        return new GetAccountRecipientsQueryParams(limit, cursor);
    }

    public static GetAccountRecipientsQueryParams of(QueryLimit limit) {
        return GetAccountRecipientsQueryParams.of(limit, null);
    }

    public static GetAccountRecipientsQueryParams of(Integer limit) {
        limit = requireNonNullElse(limit, DEFAULT_LIMIT);
        return GetAccountRecipientsQueryParams.of(QueryLimit.of(limit));
    }
}