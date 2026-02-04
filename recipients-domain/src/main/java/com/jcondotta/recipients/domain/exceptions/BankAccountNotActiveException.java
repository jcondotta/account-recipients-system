package com.jcondotta.recipients.domain.exceptions;

public class BankAccountNotActiveException extends DomainBusinessRuleViolationException {

  public BankAccountNotActiveException(String messageCode, String title, Object... args) {
    super(messageCode, title, args);
  }
}
