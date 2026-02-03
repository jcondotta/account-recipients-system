package com.jcondotta.recipients.domain.exceptions;

public class DomainBusinessRuleViolationException extends DomainException {

  protected DomainBusinessRuleViolationException(String messageCode, String title, Object... args) {
    super(messageCode, title, args);
  }
}
