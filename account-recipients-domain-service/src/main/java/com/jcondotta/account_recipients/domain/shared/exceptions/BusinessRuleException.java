package com.jcondotta.account_recipients.domain.shared.exceptions;

import java.util.Map;

public abstract class BusinessRuleException extends RuntimeException {

    protected BusinessRuleException(String message) {
        super(message);
    }

    public abstract String getType();

    public Map<String, Object> getProperties() {
        return Map.of();
    }
}