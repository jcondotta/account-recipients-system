package com.jcondotta.account_recipients.application.usecase.get_recipients.model.result;

import com.jcondotta.account_recipients.application.usecase.get_recipients.model.RecipientDetails;

import java.util.List;

import static java.util.Objects.requireNonNull;

public record GetRecipientsResult(
    List<RecipientDetails> accountRecipients, String nextCursor) {

  public GetRecipientsResult {
    requireNonNull(accountRecipients, "accountRecipients must not be null");
  }

  public static GetRecipientsResult of(
      List<RecipientDetails> accountRecipients, String nextCursor) {
    return new GetRecipientsResult(accountRecipients, nextCursor);
  }
}
