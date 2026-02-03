package com.jcondotta.recipients.get_recipients.controller.model.response;

import java.util.List;

public record GetRecipientsResponse(
    List<RecipientResponse> accountRecipients, String nextCursor) {

  public static GetRecipientsResponse of(
      List<RecipientResponse> accountRecipients, String nextCursor) {
    return new GetRecipientsResponse(accountRecipients, nextCursor);
  }
}
