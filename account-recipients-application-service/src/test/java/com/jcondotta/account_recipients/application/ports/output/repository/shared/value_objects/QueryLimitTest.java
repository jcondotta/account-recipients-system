package com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueryLimitTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 20})
    void shouldCreateQueryLimit_whenValueIsWithinRange(int validValue) {
        assertThat(QueryLimit.of(validValue))
            .satisfies(limit -> {
                assertThat(limit.value()).isEqualTo(validValue);
                assertThat(limit.toString()).hasToString(String.valueOf(validValue));
            });
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 21})
    void shouldThrowIllegalArgumentException_whenValueIsOutOfRange(int invalidValue) {
        assertThatThrownBy(() -> QueryLimit.of(invalidValue))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 1 and 20 but was: " + invalidValue);
    }

    @Test
    void shouldThrowNullPointerException_whenValueIsNull() {
        assertThatThrownBy(() -> QueryLimit.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(QueryLimit.VALUE_NOT_NULL_MESSAGE);
    }
}