package com.jcondotta.account_recipients.domain.enums;

public enum AccountStatus {
  ACTIVE,
  CANCELLED,
  PENDING,
  UNKNOWN;

  public boolean isActive() {
    return this == ACTIVE;
  }

  public boolean isPending() {
    return this == PENDING;
  }

  public boolean isCancelled() {
    return this == CANCELLED;
  }
}
