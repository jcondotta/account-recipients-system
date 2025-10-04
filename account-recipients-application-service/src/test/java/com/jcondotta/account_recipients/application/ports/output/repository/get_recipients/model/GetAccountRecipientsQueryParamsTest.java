package com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetAccountRecipientsQueryParams.DEFAULT_LIMIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetAccountRecipientsQueryParamsTest {

    private static final String nextCursor = "next-cursor-token";

    @Test
    void shouldApplyDefaultLimit_whenLimitIsNull() {
        var queryParams = new GetAccountRecipientsQueryParams(null, nextCursor);

        assertThat(queryParams.limit()).isEqualTo(DEFAULT_LIMIT);
        assertThat(queryParams.cursor()).isEqualTo(nextCursor);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 15, 20})
    void shouldAcceptLimit_whenValueIsWithinRange(Integer validLimit) {
        var params = new GetAccountRecipientsQueryParams(validLimit, nextCursor);

        assertThat(params.limit()).isEqualTo(validLimit);
        assertThat(params.cursor()).isEqualTo(nextCursor);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -50})
    void shouldThrowIllegalArgumentException_whenLimitIsBelowMinimum(Integer invalidLimit) {
        assertThatThrownBy(() -> new GetAccountRecipientsQueryParams(invalidLimit, nextCursor))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("limit must be between 1 and 20");
    }

    @ParameterizedTest
    @ValueSource(ints = {101, 150, 1000})
    void shouldThrowIllegalArgumentException_whenLimitIsAboveMaximum(Integer invalidLimit) {
        assertThatThrownBy(() -> new GetAccountRecipientsQueryParams(invalidLimit, nextCursor))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("limit must be between 1 and 20");
    }

    @Test
    void shouldCreateInstanceUsingFactoryMethod() {
        var params = GetAccountRecipientsQueryParams.of(DEFAULT_LIMIT, nextCursor);

        assertThat(params.limit()).isEqualTo(DEFAULT_LIMIT);
        assertThat(params.cursor()).isEqualTo(nextCursor);
    }
}
