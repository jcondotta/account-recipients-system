package com.jcondotta.account_recipients.domain.recipient.entity;

import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
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

class AccountRecipientTest {

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final RecipientId RECIPIENT_ID = RecipientId.newId();
    private static final RecipientName RECIPIENT_NAME_JEFFERSON = RecipientName.of("Jefferson Condotta");
    private static final Iban IBAN = Iban.of("GB82WEST12345698765432");

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2022-06-24T12:45:01Z"), ZoneOffset.UTC);
    private static final ZonedDateTime CREATED_AT = ZonedDateTime.now(CLOCK);

    @Test
    void shouldCreateAccountRecipientUsingFactoryMethod_whenAllValuesAreValid() {
        var accountRecipient = AccountRecipient.create(BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK);

        assertThat(accountRecipient.recipientId()).isNotNull();

        assertThat(accountRecipient)
                .extracting(
                        AccountRecipient::bankAccountId,
                        AccountRecipient::recipientName,
                        AccountRecipient::iban,
                        AccountRecipient::createdAt,
                        AccountRecipient::deletedAt)
                .containsExactly(
                        BANK_ACCOUNT_ID,
                        RECIPIENT_NAME_JEFFERSON,
                        IBAN,
                        ZonedDateTime.now(CLOCK),
                        null);

        assertThat(accountRecipient.isDeleted()).isFalse();
    }

    @Test
    void shouldMarkAccountRecipientAsDeleted_whenDeleteIsCalled() {
        var accountRecipient = createValidAccountRecipient();

        accountRecipient.delete(CLOCK);

        assertThat(accountRecipient.isDeleted()).isTrue();
        assertThat(accountRecipient.deletedAt()).isEqualTo(ZonedDateTime.now(CLOCK));
    }

    @Test
    void shouldNotChangeDeletedAt_whenDeleteIsCalledMoreThanOnce() {
        var accountRecipient = createValidAccountRecipient();

        accountRecipient.delete(CLOCK);
        var firstDeletedAt = accountRecipient.deletedAt();

        Clock laterClock = Clock.fixed(
                Instant.now(CLOCK).plusSeconds(5_000),
                ZoneOffset.UTC);

        accountRecipient.delete(laterClock);

        assertThat(accountRecipient.deletedAt())
                .isEqualTo(firstDeletedAt);
    }

    @Test
    void shouldHaveSameIdentity_whenRestoredWithSameRecipientId() {
        var recipientId = RecipientId.newId();

        var accountRecipient1 = AccountRecipient.restore(recipientId, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CREATED_AT, null);
        var accountRecipient2 = AccountRecipient.restore(recipientId, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CREATED_AT, null);

        assertThat(accountRecipient1.recipientId()).isEqualTo(accountRecipient2.recipientId());
    }

    @Test
    void shouldHaveDifferentIdentity_whenAccountRecipientsAreCreatedSeparately() {
        var accountRecipient1 = AccountRecipient.create(BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK);
        var accountRecipient2 = AccountRecipient.create(BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK);

        assertThat(accountRecipient1.recipientId()).isNotEqualTo(accountRecipient2.recipientId());
    }

    @Test
    void shouldThrowNullPointerException_whenClockIsNull() {
        assertThatThrownBy(() -> AccountRecipient.create(BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, null))
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
                        AccountRecipient.restore(
                                recipientId,
                                bankAccountId,
                                recipientName,
                                iban,
                                createdAt,
                                deletedAt))
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

    private AccountRecipient createValidAccountRecipient() {
        return AccountRecipient.create(BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK);
    }
}
