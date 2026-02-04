package com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.mapper.request;

import com.jcondotta.recipients.application.ports.output.repository.get_recipients.model.GetRecipientsQueryParams;
import com.jcondotta.recipients.application.ports.output.repository.shared.value_objects.PaginationCursor;
import com.jcondotta.recipients.application.ports.output.repository.shared.value_objects.QueryLimit;
import com.jcondotta.recipients.application.ports.output.repository.shared.value_objects.RecipientNamePrefix;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.model.request.GetRecipientsRestRequestParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetRecipientsRequestRestMapperTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final Integer LIMIT_10 = 10;
  private static final String CURSOR_VALUE = "encoded-cursor-123";
  private static final String NAME_PREFIX = "jeff";

  private final GetRecipientsRequestRestMapper mapper = new GetRecipientsRequestRestMapperImpl();

  @Test
  void shouldMapGetRecipientsQuery_whenAllValuesAreProvided() {
    var requestParams =
        new GetRecipientsRestRequestParams(LIMIT_10, CURSOR_VALUE, NAME_PREFIX);

    var expectedQueryLimit = QueryLimit.of(requestParams.limit());
    var expectedRecipientPrefixName = RecipientNamePrefix.of(requestParams.namePrefix());
    var expectedPaginationCursor = PaginationCursor.of(requestParams.cursor());

    var getRecipientsQuery = mapper.toQuery(BANK_ACCOUNT_UUID, requestParams);
    assertThat(getRecipientsQuery)
        .satisfies(
            query -> {
              assertThat(query.bankAccountId()).isEqualTo(BankAccountId.of(BANK_ACCOUNT_UUID));
              assertThat(query.queryParams())
                  .satisfies(
                      queryParams -> {
                        assertThat(queryParams.limit()).isEqualTo(expectedQueryLimit);
                        assertThat(queryParams.cursor()).isEqualTo(expectedPaginationCursor);
                        assertThat(queryParams.namePrefix()).isEqualTo(expectedRecipientPrefixName);
                      });
            });
  }

  @Test
  void shouldUseDefaultLimitAndNullFields_whenAllRequestParamsAreNull() {
    var requestParams = new GetRecipientsRestRequestParams(null, null, null);
    var queryParams = mapper.toQueryParams(requestParams);

    assertThat(queryParams.limit().value())
        .isEqualTo(GetRecipientsQueryParams.DEFAULT_LIMIT);
    assertThat(queryParams.cursor()).isNull();
    assertThat(queryParams.namePrefix()).isNull();
  }

  @Test
  void shouldUseDefaultLimitAndIgnoreBlankFields_whenCursorAndNamePrefixAreBlank() {
    var requestParams = new GetRecipientsRestRequestParams(null, "   ", "  ");
    var queryParams = mapper.toQueryParams(requestParams);

    assertThat(queryParams.limit().value())
        .isEqualTo(GetRecipientsQueryParams.DEFAULT_LIMIT);
    assertThat(queryParams.cursor()).isNull();
    assertThat(queryParams.namePrefix()).isNull();
  }

  @Test
  void shouldMapQueryLimitCorrectly_whenLimitIsProvided() {
    assertThat(mapper.mapQueryLimit(LIMIT_10)).extracting(QueryLimit::value).isEqualTo(LIMIT_10);
  }

  @Test
  void shouldReturnNullQueryLimit_whenLimitIsNull() {
    assertThat(mapper.mapQueryLimit(null)).isNull();
  }

  @Test
  void shouldMapPaginationCursorCorrectly_whenValueIsProvided() {
    assertThat(mapper.mapPaginationCursor(CURSOR_VALUE))
        .extracting(PaginationCursor::value)
        .isEqualTo(CURSOR_VALUE);
  }

  @ParameterizedTest
  @NullSource
  @EmptySource
  void shouldReturnNullPaginationCursor_whenValueIsNullOrBlank(String cursor) {
    assertThat(mapper.mapPaginationCursor(cursor)).isNull();
  }

  @Test
  void shouldMapNamePrefixCorrectly_whenValueIsProvided() {
    assertThat(mapper.mapNamePrefix(NAME_PREFIX))
        .extracting(RecipientNamePrefix::value)
        .isEqualTo(NAME_PREFIX);
  }

  @ParameterizedTest
  @NullSource
  @EmptySource
  void shouldReturnNullNamePrefix_whenValueIsNullOrBlank(String namePrefix) {
    assertThat(mapper.mapNamePrefix(namePrefix)).isNull();
  }

  @Test
  void shouldThrowException_whenMapBankAccountIdIsNull() {
    assertThatThrownBy(() -> mapper.mapBankAccountId(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("bankAccountId must not be null");
  }

  @Test
  void shouldThrowException_whenToQueryParamsIsCalledWithNull() {
    assertThatThrownBy(() -> mapper.toQueryParams(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("requestParams must not be null");
  }

  @Test
  void shouldThrowException_whenToQueryIsCalledWithNullBankAccountId() {
    var requestParams =
        new GetRecipientsRestRequestParams(LIMIT_10, CURSOR_VALUE, NAME_PREFIX);
    assertThatThrownBy(() -> mapper.toQuery(null, requestParams))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("bankAccountId must not be null");
  }

  @Test
  void shouldThrowException_whenToQueryIsCalledWithNullRequestParams() {
    assertThatThrownBy(() -> mapper.toQuery(BANK_ACCOUNT_UUID, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("requestParams must not be null");
  }

  @Test
  void shouldReturnNull_whenToQueryIsCalledWithNullParams() {
    assertThat(mapper.toQuery(null, null)).isNull();
  }

  @Test
  void shouldMapBankAccountIdCorrectly_whenValueIsProvided() {
    var result = mapper.mapBankAccountId(BANK_ACCOUNT_UUID);

    assertThat(result).isNotNull().extracting(BankAccountId::value).isEqualTo(BANK_ACCOUNT_UUID);
  }
}
