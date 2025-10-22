package com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model;

import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.QueryLimit;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.RecipientNamePrefix;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetAccountRecipientsQueryParamsTest {

    private static final QueryLimit QUERY_LIMIT_DEFAULT = QueryLimit.of(GetAccountRecipientsQueryParams.DEFAULT_LIMIT);
    private static final QueryLimit QUERY_LIMIT_10 = QueryLimit.of(10);

    private static final PaginationCursor PAGINATION_CURSOR = PaginationCursor.of("encoded-cursor-123");
    private static final RecipientNamePrefix RECIPIENT_NAME_PREFIX = RecipientNamePrefix.of("jeff");

    @Test
    void shouldCreateQueryParams_whenAllFieldsAreProvided() {
        var queryParams = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_10, RECIPIENT_NAME_PREFIX, PAGINATION_CURSOR);

        assertThat(queryParams)
            .satisfies(params -> {
                assertThat(params.limit()).isEqualTo(QUERY_LIMIT_10);
                assertThat(params.namePrefix()).isEqualTo(RECIPIENT_NAME_PREFIX);
                assertThat(params.cursor()).isEqualTo(PAGINATION_CURSOR);
            });
    }

    @Test
    void shouldCreateQueryParamsWithDefaultQueryLimit_whenLimitIsNull() {
        var queryParams = GetAccountRecipientsQueryParams.of(null, null, PAGINATION_CURSOR);
        assertThat(queryParams)
            .satisfies(params -> {
                assertThat(params.limit()).isEqualTo(QUERY_LIMIT_DEFAULT);
                assertThat(params.namePrefix()).isNull();
                assertThat(params.cursor()).isEqualTo(PAGINATION_CURSOR);
            });
    }

    @Test
    void shouldCreateQueryParamsWithNamePrefixNull_whenNamePrefixIsNull() {
        var queryParams = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_10, null, PAGINATION_CURSOR);
        assertThat(queryParams)
            .satisfies(params -> {
                assertThat(params.limit()).isEqualTo(QUERY_LIMIT_10);
                assertThat(params.namePrefix()).isNull();
                assertThat(params.cursor()).isEqualTo(PAGINATION_CURSOR);
            });
    }

    @Test
    void shouldCreateQueryParamsWithCursorNull_whenCursorIsNull() {
        var queryParams = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_10, RECIPIENT_NAME_PREFIX, null);
        assertThat(queryParams)
            .satisfies(params -> {
                assertThat(params.limit()).isEqualTo(QUERY_LIMIT_10);
                assertThat(params.namePrefix()).isEqualTo(RECIPIENT_NAME_PREFIX);
                assertThat(params.cursor()).isNull();
            });
    }

    @Test
    void shouldCreateQueryParamsWithQueryLimitFactoryMethod_whenQueryLimitIsValid() {
        var queryParams = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_10);
        assertThat(queryParams)
            .satisfies(params -> {
                assertThat(params.limit()).isEqualTo(QUERY_LIMIT_10);
                assertThat(params.namePrefix()).isNull();
                assertThat(params.cursor()).isNull();
            });
    }

    @Test
    void shouldCreateQueryParamsWithQueryLimitFactoryMethod_whenQueryLimitIntegerIsValid() {
        var queryLimitInteger = QUERY_LIMIT_10.value();
        var queryParams = GetAccountRecipientsQueryParams.of(queryLimitInteger);
        assertThat(queryParams)
            .satisfies(params -> {
                assertThat(params.limit()).isEqualTo(QUERY_LIMIT_10);
                assertThat(params.namePrefix()).isNull();
                assertThat(params.cursor()).isNull();
            });
    }

    @Test
    void shouldBeEqual_whenHavingSameValues() {
        var queryParams1 = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_10, RECIPIENT_NAME_PREFIX, PAGINATION_CURSOR);
        var queryParams2 = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_10, RECIPIENT_NAME_PREFIX, PAGINATION_CURSOR);

        assertThat(queryParams1).isEqualTo(queryParams2);
        assertThat(queryParams1.hashCode()).isEqualTo(queryParams2.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenFieldsDiffer() {
        var cursor2 = PaginationCursor.of("different-cursor");

        var params1 = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_10, RECIPIENT_NAME_PREFIX, PAGINATION_CURSOR);
        var params2 = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_DEFAULT, RECIPIENT_NAME_PREFIX, cursor2);

        assertThat(params1).isNotEqualTo(params2);
    }
}
