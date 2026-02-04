package com.jcondotta.recipients.domain.exceptions;

public class BankAccountNotActiveException extends DomainException implements DomainBusinessRuleViolationException {

  public BankAccountNotActiveException(String messageCode, String title, Object... args) {
    super(messageCode, title, args);
  }
}
