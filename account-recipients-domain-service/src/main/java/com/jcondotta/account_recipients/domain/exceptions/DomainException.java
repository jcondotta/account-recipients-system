package com.jcondotta.account_recipients.domain.exceptions;

public abstract class DomainException extends RuntimeException {

  private final String messageCode;
  private final String title;
  private final Object[] args;

  protected DomainException(String messageCode, String title, Object... args) {
    super(messageCode);
    this.messageCode = messageCode;
    this.title = title;
    this.args = args;
  }

  protected DomainException(String messageCode, String title, Throwable cause, Object... args) {
    super(messageCode, cause);
    this.messageCode = messageCode;
    this.title = title;
    this.args = args;
  }

  public String messageCode() {
    return messageCode;
  }

  public String title() {
    return title;
  }

  public Object[] args() {
    return args.clone();
  }
}