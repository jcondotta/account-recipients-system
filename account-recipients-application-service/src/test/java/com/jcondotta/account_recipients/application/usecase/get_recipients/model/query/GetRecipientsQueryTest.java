package com.jcondotta.account_recipients.application.usecase.get_recipients.model.query;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetRecipientsQueryParams;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.QueryLimit;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetRecipientsQueryTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final QueryLimit QUERY_LIMIT_10 = QueryLimit.of(10);
  private static final PaginationCursor PAGINATION_CURSOR =
      PaginationCursor.of("next-cursor-token");

  private static final GetRecipientsQueryParams QUERY_PARAMS =
      GetRecipientsQueryParams.of(QUERY_LIMIT_10, null, PAGINATION_CURSOR);

  @Test
  void shouldCreateQuery_whenValidArguments() {
    var query = new GetRecipientsQuery(BANK_ACCOUNT_ID, QUERY_PARAMS);

    assertThat(query.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(query.queryParams()).isEqualTo(QUERY_PARAMS);
  }

  @Test
  void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new GetRecipientsQuery(null, QUERY_PARAMS))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("bankAccountId must not be null");
  }

  @Test
  void shouldThrowNullPointerException_whenQueryParamsIsNull() {
    assertThatThrownBy(() -> new GetRecipientsQuery(BANK_ACCOUNT_ID, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("queryParams must not be null");
  }

  @Test
  void shouldCreateQueryUsingFactoryMethod_whenValidArguments() {
    var query = GetRecipientsQuery.of(BANK_ACCOUNT_ID, QUERY_PARAMS);

    assertThat(query.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(query.queryParams()).isEqualTo(QUERY_PARAMS);
  }
}
