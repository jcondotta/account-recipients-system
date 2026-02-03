package com.jcondotta.account_recipients.domain.exceptions;

import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;

public class BankAccountNotActiveException extends DomainBusinessRuleViolationException {

  public static final String BANK_ACCOUNT_NOT_ACTIVE_TEMPLATE = "bankAccount.notActive";
  public static final String BANK_ACCOUNT_NOT_ACTIVE_TITLE = "Bank account is not active";

  public BankAccountNotActiveException(BankAccountId bankAccountId) {
    super(
        BANK_ACCOUNT_NOT_ACTIVE_TEMPLATE,
        BANK_ACCOUNT_NOT_ACTIVE_TITLE,
        bankAccountId.value());
  }
}
