package com.jcondotta.account_recipients.application.usecase.get_recipients.model.query;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetRecipientsQueryParams;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;

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
