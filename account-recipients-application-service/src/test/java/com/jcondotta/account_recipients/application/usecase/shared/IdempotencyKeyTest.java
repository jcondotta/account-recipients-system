package com.jcondotta.account_recipients.application.usecase.shared;

import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdempotencyKeyTest {

    private static final UUID IDEMPOTENCY_KEY_UUID_1 = UUID.fromString("1fcaca1b-92ba-43c1-b45c-bacf92868d31");
    private static final UUID IDEMPOTENCY_KEY_UUID_2 = UUID.fromString("d063f4bd-dd1f-41d0-8f47-0d5b9195bfaa");

    @Test
    void shouldCreateIdempotencyKey_whenValueIsValid() {
        var idempotencyKey = IdempotencyKey.of(IDEMPOTENCY_KEY_UUID_1);

        assertThat(idempotencyKey)
            .isNotNull()
            .extracting(IdempotencyKey::value)
            .isEqualTo(IDEMPOTENCY_KEY_UUID_1);
    }

    @Test
    void shouldThrowNullPointerException_whenValueIsNull() {
        assertThatThrownBy(() -> IdempotencyKey.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("IdempotencyKey value must not be null");
    }

    @Test
    void shouldBeEqual_whenIdempotencyKeysHaveSameValue() {
        var idempotencyKey1 = IdempotencyKey.of(IDEMPOTENCY_KEY_UUID_1);
        var idempotencyKey2 = IdempotencyKey.of(IDEMPOTENCY_KEY_UUID_1);

        assertThat(idempotencyKey1)
            .isEqualTo(idempotencyKey2)
            .hasSameHashCodeAs(idempotencyKey2);
    }

    @Test
    void shouldNotBeEqual_whenIdempotencyKeysHaveDifferentValues() {
        var idempotencyKey1 = IdempotencyKey.of(IDEMPOTENCY_KEY_UUID_1);
        var idempotencyKey2 = IdempotencyKey.of(IDEMPOTENCY_KEY_UUID_2);

        assertThat(idempotencyKey1)
            .isNotEqualTo(idempotencyKey2);
    }

    @Test
    void shouldReturnStringRepresentation_whenCallingToString() {
        var idempotencyKey = IdempotencyKey.of(IDEMPOTENCY_KEY_UUID_1);

        assertThat(idempotencyKey.toString())
            .contains(IDEMPOTENCY_KEY_UUID_1.toString());
    }
}