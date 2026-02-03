package com.jcondotta.recipients.application.usecase.get_recipients.model.result;

import com.jcondotta.recipients.application.usecase.get_recipients.model.RecipientDetails;

import java.util.List;

import static java.util.Objects.requireNonNull;

public record GetRecipientsResult(
    List<RecipientDetails> recipients, String nextCursor) {

  public GetRecipientsResult {
    requireNonNull(recipients, "recipients must not be null");
  }

  public static GetRecipientsResult of(
      List<RecipientDetails> recipients, String nextCursor) {
    return new GetRecipientsResult(recipients, nextCursor);
  }
}
