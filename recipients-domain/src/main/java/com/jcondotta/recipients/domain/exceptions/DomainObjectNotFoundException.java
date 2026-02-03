package com.jcondotta.recipients.domain.exceptions;

public abstract class DomainObjectNotFoundException extends DomainException {

  protected DomainObjectNotFoundException(String messageCode, String title, Object... args) {
    super(messageCode, title, args);
  }

  protected DomainObjectNotFoundException(String messageCode, String title, Throwable cause, Object... args) {
    super(messageCode, title, cause, args);
  }
}
