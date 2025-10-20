package com.jcondotta.account_recipients.application.ports.output.repository.shared;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaginatedResultTest {

    private static final String ITEM_1 = "item1";
    private static final String ITEM_2 = "item2";
    private static final List<String> ITEMS_LIST = List.of(ITEM_1, ITEM_2);

    private static final String NEXT_CURSOR = "cursor123";

    @Test
    void shouldCreatePaginatedResult_whenAllFieldsAreProvided() {
        var paginatedResult = new PaginatedResult<>(ITEMS_LIST, NEXT_CURSOR);

        assertThat(paginatedResult)
            .satisfies(result -> {
                assertThat(result.items()).containsExactly(ITEM_1, ITEM_2);
                assertThat(result.nextCursor()).isEqualTo(NEXT_CURSOR);
                assertThat(result.hasNextPage()).isTrue();
            });

        var paginatedResultViaOf = PaginatedResult.of(ITEMS_LIST, NEXT_CURSOR);
        assertThat(paginatedResultViaOf).isEqualTo(paginatedResult);
    }

    @Test
    void shouldAllowNullNextCursor_whenNotProvided() {
        var paginatedResult = new PaginatedResult<>(ITEMS_LIST, null);

        assertThat(paginatedResult)
            .satisfies(result -> {
                assertThat(result.items()).containsExactly(ITEM_1, ITEM_2);
                assertThat(result.nextCursor()).isNull();
                assertThat(result.hasNextPage()).isFalse();
            });
    }

    @Test
    void shouldCreateEmptyPaginatedResult_whenEmptyMethodIsCalled() {
        assertThat(PaginatedResult.empty())
            .satisfies(result -> {
                assertThat(result.items()).isEmpty();
                assertThat(result.nextCursor()).isNull();
                assertThat(result.hasNextPage()).isFalse();
            });
    }

    @Test
    void shouldThrowNullPointerException_whenItemsIsNull() {
        Assertions.assertThatThrownBy(() -> PaginatedResult.of(null, NEXT_CURSOR))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(PaginatedResult.ITEMS_NOT_NULL_MESSAGE);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenCursorIsNotNullButEmpty() {
        Assertions.assertThatThrownBy(() -> PaginatedResult.of(ITEMS_LIST, StringUtils.EMPTY))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(PaginatedResult.NEXT_CURSOR_NOT_EMPTY_MESSAGE);
    }

    @Test
    void shouldBeEqual_whenHavingSameValues() {
        var paginatedResult1 = new PaginatedResult<>(ITEMS_LIST, NEXT_CURSOR);
        var paginatedResult2 = new PaginatedResult<>(ITEMS_LIST, NEXT_CURSOR);

        assertThat(paginatedResult1).isEqualTo(paginatedResult2);
        assertThat(paginatedResult1.hashCode()).isEqualTo(paginatedResult2.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenValuesDiffer() {
        var paginatedResult1 = new PaginatedResult<>(List.of("a"), "cursor1");
        var paginatedResult2 = new PaginatedResult<>(List.of("b"), "cursor2");

        assertThat(paginatedResult1).isNotEqualTo(paginatedResult2);
    }
}