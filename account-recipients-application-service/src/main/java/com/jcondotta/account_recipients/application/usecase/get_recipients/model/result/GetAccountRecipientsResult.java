package com.jcondotta.account_recipients.application.usecase.get_recipients.model.result;

import static java.util.Objects.requireNonNull;

import com.jcondotta.account_recipients.application.usecase.get_recipients.model.AccountRecipientDetails;
import java.util.List;

public record GetAccountRecipientsResult(
    List<AccountRecipientDetails> accountRecipients, String nextCursor) {

  public GetAccountRecipientsResult {
    requireNonNull(accountRecipients, "accountRecipients must not be null");
  }

  public static GetAccountRecipientsResult of(
      List<AccountRecipientDetails> accountRecipients, String nextCursor) {
    return new GetAccountRecipientsResult(accountRecipients, nextCursor);
  }
}
