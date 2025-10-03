package com.jcondotta.account_recipients.application.ports.output.repository.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class QueryParamsTest {

    private static final Integer DEFAULT_LIMIT = 10;

    private String cursorValue;

    @BeforeEach
    void setUp() {
        cursorValue = Base64.getEncoder().encodeToString("cursor-xyz".getBytes());
    }

    @Test
    void shouldCreateQueryParams_whenAllFieldsAreProvided() {
        var paginationCursor = new PaginationCursor(cursorValue);
        var queryParams = new QueryParams(DEFAULT_LIMIT, paginationCursor);

        assertThat(queryParams.limit()).isEqualTo(DEFAULT_LIMIT);
        assertThat(queryParams.cursor()).isEqualTo(paginationCursor);
    }

    @Test
    void shouldCreateQueryParamsUsingFactoryMethod_whenAllFieldsAreProvided() {
        var paginationCursor = new PaginationCursor(cursorValue);
        var queryParams = QueryParams.of(DEFAULT_LIMIT, paginationCursor);

        assertThat(queryParams.limit()).isEqualTo(DEFAULT_LIMIT);
        assertThat(queryParams.cursor()).isEqualTo(paginationCursor);
    }

    @Test
    void shouldCreateQueryParamsWithNullCursor_whenCursorIsNotProvided() {
        var queryParams = new QueryParams(DEFAULT_LIMIT, null);

        assertThat(queryParams.limit()).isEqualTo(DEFAULT_LIMIT);
        assertThat(queryParams.cursor()).isNull();
    }

    @Test
    void shouldCreateQueryParamsWithNullLimit_whenLimitIsNotProvided() {
        var paginationCursor = new PaginationCursor(cursorValue);
        var queryParams = new QueryParams(null, paginationCursor);

        assertThat(queryParams.limit()).isNull();
        assertThat(queryParams.cursor()).isEqualTo(paginationCursor);
    }

    @Test
    void shouldBeEqual_whenHavingSameValues() {
        var paginationCursor = new PaginationCursor(cursorValue);

        var params1 = new QueryParams(DEFAULT_LIMIT, paginationCursor);
        var params2 = new QueryParams(DEFAULT_LIMIT, paginationCursor);

        assertThat(params1).isEqualTo(params2);
        assertThat(params1.hashCode()).isEqualTo(params2.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenFieldsDiffer() {
        var paginationCursor1 = new PaginationCursor(cursorValue);
        var paginationCursor2 = new PaginationCursor(cursorValue);

        var params1 = new QueryParams(10, paginationCursor1);
        var params2 = new QueryParams(20, paginationCursor2);

        assertThat(params1).isNotEqualTo(params2);
    }
}