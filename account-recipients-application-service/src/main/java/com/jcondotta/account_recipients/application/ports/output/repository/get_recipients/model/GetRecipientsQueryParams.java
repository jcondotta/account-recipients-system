package com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model;

import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.QueryLimit;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.RecipientNamePrefix;

import static java.util.Objects.requireNonNullElse;

public record GetRecipientsQueryParams(
    QueryLimit limit, RecipientNamePrefix namePrefix, PaginationCursor cursor) {

  public static final int DEFAULT_LIMIT = 20;

  public GetRecipientsQueryParams {
    limit = requireNonNullElse(limit, QueryLimit.of(DEFAULT_LIMIT));
  }

  public static GetRecipientsQueryParams of(
      QueryLimit limit, RecipientNamePrefix namePrefix, PaginationCursor cursor) {
    return new GetRecipientsQueryParams(limit, namePrefix, cursor);
  }

  public static GetRecipientsQueryParams of(QueryLimit limit) {
    return GetRecipientsQueryParams.of(limit, null, null);
  }

  public static GetRecipientsQueryParams of(Integer limit) {
    limit = requireNonNullElse(limit, DEFAULT_LIMIT);
    return GetRecipientsQueryParams.of(QueryLimit.of(limit));
  }
}
