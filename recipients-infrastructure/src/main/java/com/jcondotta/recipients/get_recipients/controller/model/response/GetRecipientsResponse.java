package com.jcondotta.recipients.get_recipients.controller.model.response;

import java.util.List;

public record GetRecipientsResponse(
    List<RecipientResponse> recipients, String nextCursor) {

  public static GetRecipientsResponse of(
      List<RecipientResponse> recipients, String nextCursor) {
    return new GetRecipientsResponse(recipients, nextCursor);
  }
}
