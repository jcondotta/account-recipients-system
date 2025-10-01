package com.jcondotta.account_recipients.domain.shared.exceptions;

import java.io.Serializable;

public abstract class DomainObjectNotFoundException extends RuntimeException {

    private final String title;
    private final Serializable[] identifiers;

    public DomainObjectNotFoundException(String message, String title, Throwable cause, Serializable... identifiers) {
        super(message, cause);
        this.title = title;
        this.identifiers = identifiers;
    }

    public String getTitle() {
        return title;
    }

    public Serializable[] getIdentifiers() {
        return identifiers;
    }
}