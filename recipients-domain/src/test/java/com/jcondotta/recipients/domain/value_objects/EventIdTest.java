package com.jcondotta.recipients.domain.value_objects;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventIdTest {

    private static final UUID EVENT_UUID_1 =
            UUID.fromString("1fcaca1b-92ba-43c1-b45c-bacf92868d31");
    private static final UUID EVENT_UUID_2 =
            UUID.fromString("d063f4bd-dd1f-41d0-8f47-0d5b9195bfaa");

    @Test
    void shouldCreateEventId_whenValueIsValid() {
        var eventId = EventId.of(EVENT_UUID_1);

        assertThat(eventId)
                .isNotNull()
                .extracting(EventId::value)
                .isEqualTo(EVENT_UUID_1);
    }

    @Test
    void shouldThrowNullPointerException_whenValueIsNull() {
        assertThatThrownBy(() -> EventId.of(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(EventId.ID_NOT_NULL_MESSAGE);
    }

    @Test
    void shouldBeEqual_whenEventIdsHaveSameValue() {
        var eventId1 = EventId.of(EVENT_UUID_1);
        var eventId2 = EventId.of(EVENT_UUID_1);

        assertThat(eventId1).isEqualTo(eventId2).hasSameHashCodeAs(eventId2);
    }

    @Test
    void shouldNotBeEqual_whenEventIdsHaveDifferentValues() {
        var eventId1 = EventId.of(EVENT_UUID_1);
        var eventId2 = EventId.of(EVENT_UUID_2);

        assertThat(eventId1).isNotEqualTo(eventId2);
    }

    @Test
    void shouldReturnStringRepresentation_whenCallingToString() {
        var eventId = EventId.of(EVENT_UUID_1);
        assertThat(eventId).hasToString(EVENT_UUID_1.toString());
    }
}
