package com.jcondotta.account_recipients.application.ports.output.repository.shared;

import java.util.List;

public record PaginatedResult<T>(List<T> items, String nextCursor) { }