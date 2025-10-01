package com.jcondotta.account_recipients.interfaces.rest.exception_handler;

import java.net.URI;

public final class ProblemTypes {

    private ProblemTypes() {}

    private static final String BASE_PATH = "https://api.jcondotta.com/problems";
    public static final URI RESOURCE_NOT_FOUND = uri("/resource-not-found");
    public static final URI VALIDATION_ERRORS = uri("/validation-errors");

    private static URI uri(String path) {
        return URI.create(BASE_PATH.concat(path));
    }
}
