package com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.model.response;

import java.util.List;

public record GetRecipientsResponse(
    List<RecipientResponse> recipients, String nextCursor) {

  public static GetRecipientsResponse of(
      List<RecipientResponse> recipients, String nextCursor) {
    return new GetRecipientsResponse(recipients, nextCursor);
  }
}
