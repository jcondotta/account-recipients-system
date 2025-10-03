package com.jcondotta.account_recipients.application.ports.output.repository.shared;

public record QueryParams(Integer limit, PaginationCursor cursor) {

    public static QueryParams of(Integer limit, PaginationCursor cursor) {
        return new QueryParams(limit, cursor);
    }
}