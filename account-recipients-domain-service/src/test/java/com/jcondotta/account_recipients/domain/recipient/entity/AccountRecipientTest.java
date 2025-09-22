package com.jcondotta.account_recipients.domain.recipient.entity;

import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
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

    private static final AccountRecipientId ACCOUNT_RECIPIENT_ID_1 = AccountRecipientId.newId();
    private static final AccountRecipientId ACCOUNT_RECIPIENT_ID_2 = AccountRecipientId.newId();

    private static final BankAccountId BANK_ACCOUNT_ID_1 = BankAccountId.of(UUID.randomUUID());
    private static final BankAccountId BANK_ACCOUNT_ID_2 = BankAccountId.of(UUID.randomUUID());

    private static final RecipientName RECIPIENT_NAME_JEFFERSON = RecipientName.of("Jefferson Condotta");
    private static final RecipientName RECIPIENT_NAME_PATRIZIO = RecipientName.of("Patrizio Condotta");

    private static final Iban IBAN_1 = Iban.of("GB82WEST12345698765432");
    private static final Iban IBAN_2 = Iban.of("DE89370400440532013000");

    private static final ZonedDateTime CREATED_AT = ZonedDateTime.of(2024, 6, 1, 12, 0, 0, 0, ZoneOffset.UTC);

    @Test
    void shouldCreateAccountRecipient_whenAllValuesAreValid() {
        var accountRecipient = AccountRecipient.of(
            ACCOUNT_RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            RECIPIENT_NAME_JEFFERSON,
            IBAN_1,
            CREATED_AT
        );

        assertThat(accountRecipient)
            .isNotNull()
            .extracting(AccountRecipient::accountRecipientId, AccountRecipient::bankAccountId, AccountRecipient::recipientName, AccountRecipient::iban, AccountRecipient::createdAt)
            .containsExactly(ACCOUNT_RECIPIENT_ID_1, BANK_ACCOUNT_ID_1, RECIPIENT_NAME_JEFFERSON, IBAN_1, CREATED_AT);
    }

    @Test
    void shouldBeEqual_whenAccountRecipientsHaveSameValues() {
        var accountRecipient1 = AccountRecipient.of(
            ACCOUNT_RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            RECIPIENT_NAME_JEFFERSON,
            IBAN_1,
            CREATED_AT
        );
        var accountRecipient2 = AccountRecipient.of(
            ACCOUNT_RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            RECIPIENT_NAME_JEFFERSON,
            IBAN_1,
            CREATED_AT
        );

        assertThat(accountRecipient1)
            .isEqualTo(accountRecipient2)
            .hasSameHashCodeAs(accountRecipient2);
    }

    @Test
    void shouldNotBeEqual_whenAccountRecipientsHaveDifferentValues() {
        var accountRecipient1 = AccountRecipient.of(
            ACCOUNT_RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            RECIPIENT_NAME_JEFFERSON,
            IBAN_1,
            CREATED_AT
        );
        var accountRecipient2 = AccountRecipient.of(
            ACCOUNT_RECIPIENT_ID_2,
            BANK_ACCOUNT_ID_2,
            RECIPIENT_NAME_PATRIZIO,
            IBAN_2,
            CREATED_AT.plusDays(1)
        );

        assertThat(accountRecipient1)
            .isNotEqualTo(accountRecipient2);
    }

    @Test
    void shouldReturnStringRepresentation_whenCallingToString() {
        var accountRecipient = AccountRecipient.of(
            ACCOUNT_RECIPIENT_ID_1,
            BANK_ACCOUNT_ID_1,
            RECIPIENT_NAME_JEFFERSON,
            IBAN_1,
            CREATED_AT
        );

        assertThat(accountRecipient.toString())
            .contains(ACCOUNT_RECIPIENT_ID_1.toString())
            .contains(BANK_ACCOUNT_ID_1.toString())
            .contains(RECIPIENT_NAME_JEFFERSON.toString())
            .contains(IBAN_1.toString())
            .contains(CREATED_AT.toString());
    }

    @ParameterizedTest(name = "shouldThrowNullPointerException_when{0}IsNull")
    @MethodSource("nullFieldProvider")
    void shouldThrowNullPointerException_whenAnyFieldIsNull(String fieldName,
                                                            AccountRecipientId accountRecipientId,
                                                            BankAccountId bankAccountId,
                                                            RecipientName recipientName,
                                                            Iban iban,
                                                            ZonedDateTime createdAt) {

        assertThatThrownBy(() -> AccountRecipient.of(accountRecipientId, bankAccountId, recipientName, iban, createdAt)
        )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining(fieldName + " must not be null");
    }

    static Stream<Arguments> nullFieldProvider() {
        return Stream.of(
            Arguments.of("accountRecipientId", null, BANK_ACCOUNT_ID_1, RECIPIENT_NAME_JEFFERSON, IBAN_1, CREATED_AT),
            Arguments.of("bankAccountId", ACCOUNT_RECIPIENT_ID_1, null, RECIPIENT_NAME_JEFFERSON, IBAN_1, CREATED_AT),
            Arguments.of("recipientName", ACCOUNT_RECIPIENT_ID_1, BANK_ACCOUNT_ID_1, null, IBAN_1, CREATED_AT),
            Arguments.of("iban", ACCOUNT_RECIPIENT_ID_1, BANK_ACCOUNT_ID_1, RECIPIENT_NAME_JEFFERSON, null, CREATED_AT),
            Arguments.of("createdAt", ACCOUNT_RECIPIENT_ID_1, BANK_ACCOUNT_ID_1, RECIPIENT_NAME_JEFFERSON, IBAN_1, null)
        );
    }
}