package com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model;

import java.util.Objects;

public record GetAccountRecipientsQueryParams(Integer limit, String cursor) {

    public static final int DEFAULT_LIMIT = 10;

    public GetAccountRecipientsQueryParams {
        limit = Objects.requireNonNullElse(limit, DEFAULT_LIMIT);

        if (limit < 1 || limit > 20) {
            throw new IllegalArgumentException("limit must be between 1 and 20");
        }
    }

    public static GetAccountRecipientsQueryParams of(Integer limit, String cursor) {
        return new GetAccountRecipientsQueryParams(limit, cursor);
    }
}