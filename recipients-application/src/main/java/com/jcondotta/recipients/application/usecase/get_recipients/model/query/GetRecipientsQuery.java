package com.jcondotta.recipients.application.usecase.get_recipients.model.query;

import com.jcondotta.recipients.application.ports.output.repository.get_recipients.model.GetRecipientsQueryParams;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;

import static java.util.Objects.requireNonNull;

public record GetRecipientsQuery(
    BankAccountId bankAccountId, GetRecipientsQueryParams queryParams) {

  public GetRecipientsQuery {
    requireNonNull(bankAccountId, "bankAccountId must not be null");
    requireNonNull(queryParams, "queryParams must not be null");
  }

  public static GetRecipientsQuery of(
      BankAccountId bankAccountId, GetRecipientsQueryParams queryParams) {
    return new GetRecipientsQuery(bankAccountId, queryParams);
  }
}
