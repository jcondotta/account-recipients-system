package com.jcondotta.recipients.domain.exceptions;

@SuppressWarnings("java:S110")
public class BankAccountNotActiveException extends DomainBusinessRuleViolationException {

  public BankAccountNotActiveException(String messageCode, String title, Object... args) {
    super(messageCode, title, args);
  }
}
