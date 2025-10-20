package com.jcondotta.account_recipients.application.ports.output.repository.shared;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaginationCursorTest {

    private static final String ENCODED_CURSOR = "encoded-cursor-123";

    @Test
    void shouldCreatePaginationCursor_whenValueIsProvided() {
        var paginationCursor = new PaginationCursor(ENCODED_CURSOR);
        assertThat(paginationCursor)
            .satisfies(cursor -> {
                assertThat(cursor.value()).isEqualTo(ENCODED_CURSOR);
                assertThat(cursor.toString()).isEqualTo(ENCODED_CURSOR);
            });
    }

    @Test
    void shouldCreatePaginationCursorUsingFactoryMethod_whenValueIsProvided() {
        var paginationCursor = PaginationCursor.of(ENCODED_CURSOR);
        assertThat(paginationCursor.value()).isEqualTo(ENCODED_CURSOR);
    }

    @Test
    void shouldThrowNullPointerException_whenValueIsNull() {
        assertThatThrownBy(() -> new PaginationCursor(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(PaginationCursor.VALUE_NOT_NULL_MESSAGE);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenValueIsBlank() {
        assertThatThrownBy(() -> new PaginationCursor(StringUtils.EMPTY))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(PaginationCursor.VALUE_NOT_BLANK_MESSAGE);
    }

    @Test
    void shouldBeEqual_whenValuesAreSame() {
        var paginationCursor1 = new PaginationCursor(ENCODED_CURSOR);
        var paginationCursor2 = new PaginationCursor(ENCODED_CURSOR);

        assertThat(paginationCursor1).isEqualTo(paginationCursor2);
        assertThat(paginationCursor1.hashCode()).isEqualTo(paginationCursor2.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenValuesDiffer() {
        var paginationCursor1 = new PaginationCursor("value1");
        var paginationCursor2 = new PaginationCursor("value2");

        assertThat(paginationCursor1).isNotEqualTo(paginationCursor2);
    }
}