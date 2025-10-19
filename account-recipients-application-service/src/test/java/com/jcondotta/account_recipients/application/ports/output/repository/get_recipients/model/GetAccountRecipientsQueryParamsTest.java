package com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model;

import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.QueryLimit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetAccountRecipientsQueryParamsTest {

    private static final QueryLimit QUERY_LIMIT_DEFAULT = QueryLimit.of(GetAccountRecipientsQueryParams.DEFAULT_LIMIT);
    private static final QueryLimit QUERY_LIMIT_20 = QueryLimit.of(20);

    private static final PaginationCursor PAGINATION_CURSOR = PaginationCursor.of("encoded-cursor-123");

    @Test
    void shouldCreateQueryParams_whenAllFieldsAreProvided() {
        var queryParams = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_20, PAGINATION_CURSOR);

        assertThat(queryParams)
            .satisfies(params -> {
                assertThat(params.limit()).isEqualTo(QUERY_LIMIT_20);
                assertThat(params.cursor()).isEqualTo(PAGINATION_CURSOR);
            });
    }

    @Test
    void shouldCreateQueryParamsWithDefaultQueryLimit_whenLimitIsNull() {
        var queryParams = GetAccountRecipientsQueryParams.of(null, PAGINATION_CURSOR);
        assertThat(queryParams)
            .satisfies(params -> {
                assertThat(params.limit()).isEqualTo(QUERY_LIMIT_DEFAULT);
                assertThat(params.cursor()).isEqualTo(PAGINATION_CURSOR);
            });
    }

    @Test
    void shouldThrowNullPointerException_whenCursorIsNull() {
        var queryParams = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_20, null);
        assertThat(queryParams)
            .satisfies(params -> {
                assertThat(params.limit()).isEqualTo(QUERY_LIMIT_20);
                assertThat(params.cursor()).isNull();
            });
    }

    @Test
    void shouldBeEqual_whenHavingSameValues() {
        var queryParams1 = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_20, PAGINATION_CURSOR);
        var queryParams2 = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_20, PAGINATION_CURSOR);

        assertThat(queryParams1).isEqualTo(queryParams2);
        assertThat(queryParams1.hashCode()).isEqualTo(queryParams2.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenFieldsDiffer() {
        var cursor2 = PaginationCursor.of("different-cursor");

        var params1 = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_20, PAGINATION_CURSOR);
        var params2 = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_DEFAULT, cursor2);

        assertThat(params1).isNotEqualTo(params2);
    }
}
