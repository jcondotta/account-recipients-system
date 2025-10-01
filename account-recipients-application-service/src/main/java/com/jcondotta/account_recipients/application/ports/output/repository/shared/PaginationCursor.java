package com.jcondotta.account_recipients.application.ports.output.repository.shared;

public record PaginationCursor(String value) {

    public static PaginationCursor of(String value) {
        return new PaginationCursor(value);
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}