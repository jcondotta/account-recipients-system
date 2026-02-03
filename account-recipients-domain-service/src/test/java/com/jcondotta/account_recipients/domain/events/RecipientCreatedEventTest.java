package com.jcondotta.account_recipients.domain.events;

import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import com.jcondotta.account_recipients.domain.value_objects.Iban;
import com.jcondotta.account_recipients.domain.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.value_objects.RecipientName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientCreatedEventTest {

  private static final RecipientId RECIPIENT_ID_1 = RecipientId.newId();
  private static final RecipientId RECIPIENT_ID_2 = RecipientId.newId();

  private static final BankAccountId BANK_ACCOUNT_ID_1 = BankAccountId.of(UUID.randomUUID());
  private static final BankAccountId BANK_ACCOUNT_ID_2 = BankAccountId.of(UUID.randomUUID());

  private static final RecipientName RECIPIENT_NAME_JEFFERSON = RecipientName.of("Jefferson Condotta");
  private static final RecipientName RECIPIENT_NAME_PATRIZIO = RecipientName.of("Patrizio Condotta");

  private static final Iban IBAN_1 = Iban.of("GB82WEST12345698765432");
  private static final Iban IBAN_2 = Iban.of("DE89370400440532013000");

  private static final Clock CLOCK = Clock.fixed(Instant.parse("2022-06-24T12:45:01Z"), ZoneOffset.UTC);
  private static final ZonedDateTime OCCURRED_AT = ZonedDateTime.now(CLOCK);

  @Test
  void shouldCreateRecipientCreatedEvent_whenAllValuesAreValid() {
    var event =
        new RecipientCreatedEvent(
            RECIPIENT_ID_1,
            RECIPIENT_NAME_JEFFERSON,
            BANK_ACCOUNT_ID_1,
            IBAN_1,
            OCCURRED_AT);

    assertThat(event)
        .extracting(
            RecipientCreatedEvent::recipientId,
            RecipientCreatedEvent::recipientName,
            RecipientCreatedEvent::bankAccountId,
            RecipientCreatedEvent::iban,
            RecipientCreatedEvent::occurredAt)
        .containsExactly(
            RECIPIENT_ID_1,
            RECIPIENT_NAME_JEFFERSON,
            BANK_ACCOUNT_ID_1,
            IBAN_1,
            OCCURRED_AT);
  }

  @Test
  void shouldCreateRecipientCreatedEventUsingZonedDateTimeFactory() {
    var event =
        RecipientCreatedEvent.of(
            RECIPIENT_ID_1,
            RECIPIENT_NAME_JEFFERSON,
            BANK_ACCOUNT_ID_1,
            IBAN_1,
            OCCURRED_AT);

    assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
  }

  @Test
  void shouldBeEqual_whenEventsHaveSameValues() {
    var event1 =
        RecipientCreatedEvent.of(
            RECIPIENT_ID_1,
            RECIPIENT_NAME_JEFFERSON,
            BANK_ACCOUNT_ID_1,
            IBAN_1,
            OCCURRED_AT);

    var event2 =
        RecipientCreatedEvent.of(
            RECIPIENT_ID_1,
            RECIPIENT_NAME_JEFFERSON,
            BANK_ACCOUNT_ID_1,
            IBAN_1,
            OCCURRED_AT);

    assertThat(event1)
        .isEqualTo(event2)
        .hasSameHashCodeAs(event2);
  }

  @Test
  void shouldNotBeEqual_whenEventsHaveDifferentValues() {
    var event1 =
        RecipientCreatedEvent.of(
            RECIPIENT_ID_1,
            RECIPIENT_NAME_JEFFERSON,
            BANK_ACCOUNT_ID_1,
            IBAN_1,
            OCCURRED_AT);

    var event2 =
        RecipientCreatedEvent.of(
            RECIPIENT_ID_2,
            RECIPIENT_NAME_PATRIZIO,
            BANK_ACCOUNT_ID_2,
            IBAN_2,
            OCCURRED_AT.plusDays(1));

    assertThat(event1).isNotEqualTo(event2);
  }

  @Test
  void shouldExposeMeaningfulToString() {
    var event =
        RecipientCreatedEvent.of(
            RECIPIENT_ID_1,
            RECIPIENT_NAME_JEFFERSON,
            BANK_ACCOUNT_ID_1,
            IBAN_1,
            OCCURRED_AT);

    assertThat(event.toString())
        .contains(RECIPIENT_ID_1.toString())
        .contains(RECIPIENT_NAME_JEFFERSON.toString())
        .contains(BANK_ACCOUNT_ID_1.toString())
        .contains(IBAN_1.toString())
        .contains(OCCURRED_AT.toString());
  }

  @ParameterizedTest(name = "{0} must not be null")
  @MethodSource("nullFieldProvider")
  void shouldThrowNullPointerException_whenAnyRequiredFieldIsNull(
      String fieldName,
      RecipientId recipientId,
      RecipientName recipientName,
      BankAccountId bankAccountId,
      Iban iban,
      ZonedDateTime occurredAt) {
    assertThatThrownBy(
        () ->
            new RecipientCreatedEvent(
                recipientId,
                recipientName,
                bankAccountId,
                iban,
                occurredAt))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining(fieldName + " must not be null");
  }

  static Stream<Arguments> nullFieldProvider() {
    return Stream.of(
        Arguments.of("recipientId", null, RECIPIENT_NAME_JEFFERSON, BANK_ACCOUNT_ID_1, IBAN_1, OCCURRED_AT),
        Arguments.of("recipientName", RECIPIENT_ID_1, null, BANK_ACCOUNT_ID_1, IBAN_1, OCCURRED_AT),
        Arguments.of("bankAccountId", RECIPIENT_ID_1, RECIPIENT_NAME_JEFFERSON, null, IBAN_1, OCCURRED_AT),
        Arguments.of("iban", RECIPIENT_ID_1, RECIPIENT_NAME_JEFFERSON, BANK_ACCOUNT_ID_1, null, OCCURRED_AT),
        Arguments.of("occurredAt", RECIPIENT_ID_1, RECIPIENT_NAME_JEFFERSON, BANK_ACCOUNT_ID_1, IBAN_1, null)
    );
  }
}