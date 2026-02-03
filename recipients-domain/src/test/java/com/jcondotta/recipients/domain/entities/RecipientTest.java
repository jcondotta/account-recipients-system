package com.jcondotta.recipients.domain.entities;

import com.jcondotta.recipients.domain.value_objects.Iban;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.RecipientName;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
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

class RecipientTest {

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final RecipientId RECIPIENT_ID = RecipientId.newId();
    private static final RecipientName RECIPIENT_NAME_JEFFERSON = RecipientName.of("Jefferson Condotta");
    private static final Iban IBAN = Iban.of("GB82WEST12345698765432");

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2022-06-24T12:45:01Z"), ZoneOffset.UTC);
    private static final ZonedDateTime CREATED_AT = ZonedDateTime.now(CLOCK);

    @Test
    void shouldCreateAccountRecipientUsingFactoryMethod_whenAllValuesAreValid() {
        var recipient = Recipient.create(BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK);

        assertThat(recipient.getRecipientId()).isNotNull();

        assertThat(recipient)
                .extracting(
                        Recipient::getBankAccountId,
                        Recipient::getRecipientName,
                        Recipient::getIban,
                        Recipient::getCreatedAt,
                        Recipient::getDeletedAt)
                .containsExactly(
                        BANK_ACCOUNT_ID,
                        RECIPIENT_NAME_JEFFERSON,
                        IBAN,
                        ZonedDateTime.now(CLOCK),
                        null);

        assertThat(recipient.isDeleted()).isFalse();
    }

    @Test
    void shouldMarkAccountRecipientAsDeleted_whenDeleteIsCalled() {
        var recipient = createValidAccountRecipient();

        recipient.delete(CLOCK);

        assertThat(recipient.isDeleted()).isTrue();
        assertThat(recipient.getDeletedAt()).isEqualTo(ZonedDateTime.now(CLOCK));
    }

    @Test
    void shouldNotChangeDeletedAt_whenDeleteIsCalledMoreThanOnce() {
        var recipient = createValidAccountRecipient();

        recipient.delete(CLOCK);
        var firstDeletedAt = recipient.getDeletedAt();

        Clock laterClock = Clock.fixed(
                Instant.now(CLOCK).plusSeconds(5_000),
                ZoneOffset.UTC);

        recipient.delete(laterClock);

        assertThat(recipient.getDeletedAt())
                .isEqualTo(firstDeletedAt);
    }

    @Test
    void shouldThrowNullPointerException_whenClockIsNull() {
        assertThatThrownBy(() -> Recipient.create(BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("clock must not be null");
    }

    @ParameterizedTest(name = "{0} should throw NullPointerException when null")
    @MethodSource("nullFieldProvider")
    void shouldThrowNullPointerException_whenAnyRequiredFieldIsNull(
            String fieldName,
            RecipientId recipientId,
            BankAccountId bankAccountId,
            RecipientName recipientName,
            Iban iban,
            ZonedDateTime createdAt,
            ZonedDateTime deletedAt) {

        assertThatThrownBy(
                () ->
                        Recipient.restore(
                                recipientId,
                                bankAccountId,
                                recipientName,
                                iban,
                                createdAt))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining(fieldName + " must not be null");
    }

    static Stream<Arguments> nullFieldProvider() {
        return Stream.of(
                Arguments.of(
                        "recipientId",
                        null,
                        BANK_ACCOUNT_ID,
                        RECIPIENT_NAME_JEFFERSON,
                        IBAN,
                        CREATED_AT,
                        null),
                Arguments.of(
                        "bankAccountId",
                        RECIPIENT_ID,
                        null,
                        RECIPIENT_NAME_JEFFERSON,
                        IBAN,
                        CREATED_AT,
                        null),
                Arguments.of(
                        "recipientName",
                        RECIPIENT_ID,
                        BANK_ACCOUNT_ID,
                        null,
                        IBAN,
                        CREATED_AT,
                        null),
                Arguments.of(
                        "iban",
                        RECIPIENT_ID,
                        BANK_ACCOUNT_ID,
                        RECIPIENT_NAME_JEFFERSON,
                        null,
                        CREATED_AT,
                        null),
                Arguments.of(
                        "createdAt",
                        RECIPIENT_ID,
                        BANK_ACCOUNT_ID,
                        RECIPIENT_NAME_JEFFERSON,
                        IBAN,
                        null,
                        null));
    }

    private Recipient createValidAccountRecipient() {
        return Recipient.create(BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK);
    }

    @Test
    void shouldBeEqual_whenRecipientIdIsSame() {
        var recipient1 =
            Recipient.restore(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CREATED_AT);

        var recipient2 =
            Recipient.restore(RECIPIENT_ID, BANK_ACCOUNT_ID, RecipientName.of("different name"), IBAN, CREATED_AT);

        assertThat(recipient1)
            .isEqualTo(recipient2)
            .hasSameHashCodeAs(recipient2);
    }

    @Test
    void shouldNotBeEqual_whenRecipientIdIsDifferent() {
        var recipient1 =
            Recipient.restore(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CREATED_AT);

        var recipient2 =
            Recipient.restore(RecipientId.newId(), BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CREATED_AT);

        assertThat(recipient1).isNotEqualTo(recipient2);
    }

    @Test
    void shouldBeEqualToItself() {
        var recipient =
            Recipient.restore(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CREATED_AT);

        assertThat(recipient).isEqualTo(recipient);
    }

    @Test
    void shouldNotBeEqualToNullOrOtherType() {
        var recipient =
            Recipient.restore(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CREATED_AT);

        assertThat(recipient)
            .isNotEqualTo(null)
            .isNotEqualTo(new Object());
    }
}
