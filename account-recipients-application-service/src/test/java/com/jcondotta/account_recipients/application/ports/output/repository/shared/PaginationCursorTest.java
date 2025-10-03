package com.jcondotta.account_recipients.application.ports.output.repository.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaginationCursorTest {

    private String cursorValue;

    @BeforeEach
    void setUp() {
        cursorValue = Base64.getEncoder().encodeToString("cursor-xyz".getBytes());
    }

    @Test
    void shouldCreatePaginationCursor_whenValueIsProvided() {
        var cursor = new PaginationCursor(cursorValue);

        assertThat(cursor.value()).isEqualTo(cursorValue);
    }

    @Test
    void shouldCreatePaginationCursorUsingFactoryMethod_whenValueIsProvided() {
        var cursor = PaginationCursor.of(cursorValue);

        assertThat(cursor.value()).isEqualTo(cursorValue);
    }

    @Test
    void shouldThrowNullPointerException_whenValueIsNull() {
        assertThatThrownBy(() -> new PaginationCursor(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(PaginationCursor.VALUE_NOT_NULL_MESSAGE);
    }

    @Test
    void shouldBeEqual_whenValuesAreSame() {
        var paginationCursor1 = new PaginationCursor(cursorValue);
        var paginationCursor2 = new PaginationCursor(cursorValue);

        assertThat(paginationCursor1).isEqualTo(paginationCursor2);
        assertThat(paginationCursor1.hashCode()).isEqualTo(paginationCursor2.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenValuesDiffer() {
        var paginationCursor1 = new PaginationCursor("value1");
        var paginationCursor2 = new PaginationCursor("value2");

        assertThat(paginationCursor1).isNotEqualTo(paginationCursor2);
    }

    @Test
    void shouldContainValueInStringRepresentation() {
        var cursor = new PaginationCursor(cursorValue);
        assertThat(cursor).hasToString(cursorValue);
    }
}