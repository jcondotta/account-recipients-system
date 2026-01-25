package com.jcondotta.account_recipients.domain.recipient.events;

import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.*;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientDeletedEventTest {

  private static final RecipientId RECIPIENT_ID_1 = RecipientId.newId();
  private static final RecipientId RECIPIENT_ID_2 = RecipientId.newId();

  private static final BankAccountId BANK_ACCOUNT_ID_1 =
      BankAccountId.of(UUID.randomUUID());
  private static final BankAccountId BANK_ACCOUNT_ID_2 =
      BankAccountId.of(UUID.randomUUID());

  private static final ZonedDateTime ZONED_DATE_TIME =
      ZonedDateTime.of(2024, 6, 1, 12, 0, 0, 0, ZoneOffset.UTC);

  private static final Clock FIXED_CLOCK =
      Clock.fixed(ZONED_DATE_TIME.toInstant(), ZoneId.of("UTC"));

  static Stream<Arguments> nullFieldProvider() {
    return Stream.of(
        Arguments.of(
            "recipientId",
            null,
            BANK_ACCOUNT_ID_1,
            ZONED_DATE_TIME.toInstant(),
            ZONED_DATE_TIME.getZone()),
        Arguments.of(
            "bankAccountId",
            RECIPIENT_ID_1,
            null,
            ZONED_DATE_TIME.toInstant(),
            ZONED_DATE_TIME.getZone()),
        Arguments.of(
            "occurredAt",
            RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            null,
            ZONED_DATE_TIME.getZone()),
        Arguments.of(
            "occurredAtZone",
            RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            ZONED_DATE_TIME.toInstant(),
            null));
  }

  @Test
  void shouldCreateRecipientDeletedEvent_whenAllValuesAreValid() {
    var event =
        new RecipientDeletedEvent(
            RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            ZONED_DATE_TIME.toInstant(),
            ZONED_DATE_TIME.getZone());

    assertThat(event)
        .isNotNull()
        .extracting(
            RecipientDeletedEvent::recipientId,
            RecipientDeletedEvent::bankAccountId,
            RecipientDeletedEvent::occurredAt,
            RecipientDeletedEvent::occurredAtZone)
        .containsExactly(
            RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            ZONED_DATE_TIME.toInstant(),
            ZONED_DATE_TIME.getZone());
  }

  @Test
  void shouldCreateRecipientDeletedEventUsingZonedDateTimeFactory_whenValuesAreValid() {
    var event =
        RecipientDeletedEvent.of(
            RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            ZONED_DATE_TIME);

    assertThat(event.occurredAt()).isEqualTo(ZONED_DATE_TIME.toInstant());
    assertThat(event.occurredAtZone()).isEqualTo(ZONED_DATE_TIME.getZone());
  }

  @Test
  void shouldCreateRecipientDeletedEventUsingClockFactory_whenValuesAreValid() {
    var event =
        RecipientDeletedEvent.of(
            RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            FIXED_CLOCK);

    assertThat(event.occurredAt()).isEqualTo(ZONED_DATE_TIME.toInstant());
    assertThat(event.occurredAtZone()).isEqualTo(FIXED_CLOCK.getZone());
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

    assertThat(event1).isEqualTo(event2).hasSameHashCodeAs(event2);
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
        .contains(ZONED_DATE_TIME.toInstant().toString())
        .contains(ZONED_DATE_TIME.getZone().toString());
  }

  @ParameterizedTest(name = "shouldThrowNullPointerException_when{0}IsNull")
  @MethodSource("nullFieldProvider")
  void shouldThrowNullPointerException_whenAnyFieldIsNull(
      String fieldName,
      RecipientId recipientId,
      BankAccountId bankAccountId,
      Instant occurredAt,
      ZoneId occurredAtZone) {

    assertThatThrownBy(
        () ->
            new RecipientDeletedEvent(
                recipientId,
                bankAccountId,
                occurredAt,
                occurredAtZone))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining(fieldName + " must not be null");
  }
}
