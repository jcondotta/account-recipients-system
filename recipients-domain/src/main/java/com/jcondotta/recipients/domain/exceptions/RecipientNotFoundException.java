package com.jcondotta.recipients.domain.exceptions;

import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.RecipientId;

@SuppressWarnings("java:S110")
public class RecipientNotFoundException extends DomainObjectNotFoundException {

  public static final String RECIPIENT_NOT_FOUND_TEMPLATE = "recipient.notFound";
  public static final String RECIPIENT_NOT_FOUND_TITLE = "Recipient not found";

  public RecipientNotFoundException(BankAccountId bankAccountId, RecipientId recipientId, Throwable cause) {
    super(
        RECIPIENT_NOT_FOUND_TEMPLATE,
        RECIPIENT_NOT_FOUND_TITLE,
        cause,
        bankAccountId.value(),
        recipientId.value());
  }

  public RecipientNotFoundException(BankAccountId bankAccountId, RecipientId recipientId) {
    super(
        RECIPIENT_NOT_FOUND_TEMPLATE,
        RECIPIENT_NOT_FOUND_TITLE,
        bankAccountId.value(),
        recipientId.value());
  }
}
