package com.jcondotta.account_recipients.domain.shared.exceptions;

import java.io.Serializable;

public class DomainObjectNotFoundException extends RuntimeException {

    private final Serializable[] identifiers;

    public DomainObjectNotFoundException(String message, Serializable... identifiers) {
        super(message);
        this.identifiers = identifiers;
    }

    public Serializable[] getIdentifiers() {
        return identifiers;
    }
}