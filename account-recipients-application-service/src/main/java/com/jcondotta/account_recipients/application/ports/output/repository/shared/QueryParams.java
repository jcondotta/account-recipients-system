package com.jcondotta.account_recipients.application.ports.output.repository.shared;

public record QueryParams(Integer limit, PaginationCursor cursor) {}