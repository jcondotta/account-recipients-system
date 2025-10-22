package com.jcondotta.account_recipients.application.ports.output.repository.shared.model;

import java.util.List;

import java.util.Objects;

public record PaginatedResult<T>(List<T> items, String nextCursor) {

    public static final String ITEMS_NOT_NULL_MESSAGE = "items must not be null";
    public static final String NEXT_CURSOR_NOT_EMPTY_MESSAGE = "nextCursor must not be empty when present";

    public PaginatedResult {
        Objects.requireNonNull(items, ITEMS_NOT_NULL_MESSAGE);

        if (Objects.nonNull(nextCursor) && nextCursor.isBlank()) {
            throw new IllegalArgumentException(NEXT_CURSOR_NOT_EMPTY_MESSAGE);
        }
    }

    public static <T> PaginatedResult<T> of(List<T> items, String nextCursor) {
        return new PaginatedResult<>(items, nextCursor);
    }

    public static <T> PaginatedResult<T> empty() {
        return new PaginatedResult<>(List.of(), null);
    }

    public boolean hasNextPage() {
        return nextCursor != null;
    }
}