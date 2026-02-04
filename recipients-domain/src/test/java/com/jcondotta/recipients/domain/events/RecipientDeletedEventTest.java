package com.jcondotta.recipients.domain.events;

import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.EventId;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientDeletedEventTest {

  private static final EventId EVENT_ID_1 = EventId.newEventId();

  private static final RecipientId RECIPIENT_ID_1 = RecipientId.newId();
  private static final RecipientId RECIPIENT_ID_2 = RecipientId.newId();

  private static final BankAccountId BANK_ACCOUNT_ID_1 =
      BankAccountId.of(UUID.randomUUID());
  private static final BankAccountId BANK_ACCOUNT_ID_2 =
      BankAccountId.of(UUID.randomUUID());

  private static final ZonedDateTime ZONED_DATE_TIME =
      ZonedDateTime.of(2024, 6, 1, 12, 0, 0, 0, ZoneOffset.UTC);

  @Test
  void shouldCreateRecipientDeletedEvent_whenAllValuesAreValid() {
    var event =
        new RecipientDeletedEvent(
            EVENT_ID_1,
            RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            ZONED_DATE_TIME);

    assertThat(event)
        .isNotNull()
        .extracting(
            RecipientDeletedEvent::eventId,
            RecipientDeletedEvent::recipientId,
            RecipientDeletedEvent::bankAccountId,
            RecipientDeletedEvent::occurredAt)
        .containsExactly(
            EVENT_ID_1,
            RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            ZONED_DATE_TIME);
  }

  @Test
  void shouldCreateRecipientDeletedEventUsingZonedDateTimeFactory_whenValuesAreValid() {
    var event =
        RecipientDeletedEvent.of(
            RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            ZONED_DATE_TIME);

    assertThat(event.occurredAt()).isEqualTo(ZONED_DATE_TIME);
  }

  @Test
  void shouldBeEqual_whenRecipientDeletedEventsHaveSameValues() {
    var event1 =
        RecipientDeletedEvent.of(
            RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            ZONED_DATE_TIME);

    var event2 =
        RecipientDeletedEvent.of(
            RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            ZONED_DATE_TIME);

    assertThat(event1.eventId()).isNotEqualTo(event2.eventId());

    assertThat(event1)
        .usingRecursiveComparison()
        .ignoringFields("eventId")
        .isEqualTo(event2);
  }

  @Test
  void shouldNotBeEqual_whenRecipientDeletedEventsHaveDifferentValues() {
    var event1 =
        RecipientDeletedEvent.of(
            RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            ZONED_DATE_TIME);

    var event2 =
        RecipientDeletedEvent.of(
            RECIPIENT_ID_2,
            BANK_ACCOUNT_ID_2,
            ZONED_DATE_TIME.plusDays(1));

    assertThat(event1).isNotEqualTo(event2);
  }

  @Test
  void shouldReturnStringRepresentation_whenCallingToString() {
    var event =
        RecipientDeletedEvent.of(
            RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            ZONED_DATE_TIME);

    assertThat(event.toString())
        .contains(RECIPIENT_ID_1.toString())
        .contains(BANK_ACCOUNT_ID_1.toString())
        .contains(ZONED_DATE_TIME.toString());
  }

  @ParameterizedTest(name = "shouldThrowNullPointerException_when{0}IsNull")
  @MethodSource("nullFieldProvider")
  void shouldThrowNullPointerException_whenAnyFieldIsNull(
      String fieldName,
      EventId eventId,
      RecipientId recipientId,
      BankAccountId bankAccountId,
      ZonedDateTime occurredAt) {

    assertThatThrownBy(
        () ->
            new RecipientDeletedEvent(
                eventId,
                recipientId,
                bankAccountId,
                occurredAt))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining(fieldName + " must not be null");
  }

  static Stream<Arguments> nullFieldProvider() {
    return Stream.of(
        Arguments.of("eventId", null, RECIPIENT_ID_1, BANK_ACCOUNT_ID_1, ZONED_DATE_TIME),
        Arguments.of("recipientId", EVENT_ID_1, null, BANK_ACCOUNT_ID_1, ZONED_DATE_TIME),
        Arguments.of("bankAccountId", EVENT_ID_1, RECIPIENT_ID_1, null, ZONED_DATE_TIME),
        Arguments.of("occurredAt", EVENT_ID_1, RECIPIENT_ID_1, BANK_ACCOUNT_ID_1, null)
    );
  }
}
