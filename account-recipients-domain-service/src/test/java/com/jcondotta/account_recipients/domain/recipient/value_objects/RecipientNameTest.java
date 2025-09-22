package com.jcondotta.account_recipients.domain.recipient.value_objects;

import com.jcondotta.account_recipients.domain.argument_provider.BlankValuesArgumentProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientNameTest {

    private static final String RECIPIENT_NAME_JEFFERSON = "Jefferson Condotta";
    private static final String RECIPIENT_NAME_PATRIZIO = "Patrizio Condotta";

    @Test
    void shouldCreateRecipientName_whenValueIsValid() {
        assertThat(RecipientName.of(RECIPIENT_NAME_JEFFERSON))
            .isNotNull()
            .extracting(RecipientName::value)
            .isEqualTo(RECIPIENT_NAME_JEFFERSON);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenNameIsNull() {
        assertThatThrownBy(() -> RecipientName.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(RecipientName.NAME_NOT_NULL);
    }

    @ParameterizedTest
    @ArgumentsSource(BlankValuesArgumentProvider.class)
    void shouldThrowIllegalArgumentException_whenNameIsBlank(String blankValue) {
        assertThatThrownBy(() -> RecipientName.of(blankValue))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(RecipientName.NAME_NOT_BLANK);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenNameIsTooLong() {
        var longName = "A".repeat(51);

        assertThatThrownBy(() -> RecipientName.of(longName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(RecipientName.NAME_TOO_LONG);
    }

    @Test
    void shouldBeEqual_whenNamesHaveSameValue() {
        var recipientName1 = RecipientName.of(RECIPIENT_NAME_JEFFERSON);
        var recipientName2 = RecipientName.of(RECIPIENT_NAME_JEFFERSON);

        assertThat(recipientName1)
            .isEqualTo(recipientName2)
            .hasSameHashCodeAs(recipientName2);
    }

    @Test
    void shouldNotBeEqual_whenNamesHaveDifferentValues() {
        var recipientName1 = RecipientName.of(RECIPIENT_NAME_JEFFERSON);
        var recipientName2 = RecipientName.of(RECIPIENT_NAME_PATRIZIO);

        assertThat(recipientName1)
            .isNotEqualTo(recipientName2);
    }

    @Test
    void shouldReturnStringRepresentation_whenCallingToString() {
        var recipientName = RecipientName.of(RECIPIENT_NAME_JEFFERSON);
        assertThat(recipientName.toString())
            .isEqualTo(RECIPIENT_NAME_JEFFERSON);
    }

}