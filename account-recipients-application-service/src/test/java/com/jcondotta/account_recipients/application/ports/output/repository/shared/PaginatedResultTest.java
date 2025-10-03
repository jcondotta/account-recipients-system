package com.jcondotta.account_recipients.application.ports.output.repository.shared;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaginatedResultTest {

    @Test
    void shouldCreatePaginatedResult_whenAllFieldsAreProvided() {
        var items = List.of("item1", "item2");
        var result = new PaginatedResult<>(items, "cursor123");

        assertThat(result.items()).containsExactly("item1", "item2");
        assertThat(result.nextCursor()).isEqualTo("cursor123");
    }

    @Test
    void shouldAllowNullNextCursor_whenNotProvided() {
        var items = List.of("item1");
        var result = new PaginatedResult<>(items, null);

        assertThat(result.items()).containsExactly("item1");
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void shouldBeEqual_whenHavingSameValues() {
        var items = List.of("a", "b");
        var r1 = new PaginatedResult<>(items, "cursorX");
        var r2 = new PaginatedResult<>(items, "cursorX");

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenValuesDiffer() {
        var r1 = new PaginatedResult<>(List.of("a"), "cursor1");
        var r2 = new PaginatedResult<>(List.of("b"), "cursor2");

        assertThat(r1).isNotEqualTo(r2);
    }

    @Test
    void shouldContainValuesInToString() {
        var result = new PaginatedResult<>(List.of("foo"), "bar");

        assertThat(result.toString())
            .contains("foo")
            .contains("bar");
    }
}