package com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientNamePrefixTest {

    private static final String NAME_PREFIX = "jeff";

    @Test
    void shouldCreateRecipientNamePrefix_whenValueIsProvided() {
        var namePrefix = new RecipientNamePrefix(NAME_PREFIX);
        assertThat(namePrefix)
            .satisfies(prefix -> {
                assertThat(prefix.value()).isEqualTo(NAME_PREFIX);
                assertThat(prefix.toString()).isEqualTo(NAME_PREFIX);
            });
    }

    @Test
    void shouldCreateRecipientNamePrefixUsingFactoryMethod_whenValueIsProvided() {
        var namePrefix = RecipientNamePrefix.of(NAME_PREFIX);
        assertThat(namePrefix.value()).isEqualTo(NAME_PREFIX);
    }

    @Test
    void shouldThrowNullPointerException_whenValueIsNull() {
        assertThatThrownBy(() -> new RecipientNamePrefix(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(RecipientNamePrefix.VALUE_NOT_NULL_MESSAGE);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenValueIsBlank() {
        assertThatThrownBy(() -> new RecipientNamePrefix(StringUtils.EMPTY))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(RecipientNamePrefix.VALUE_NOT_BLANK_MESSAGE);
    }

    @Test
    void shouldBeEqual_whenValuesAreSame() {
        var prefix1 = new RecipientNamePrefix(NAME_PREFIX);
        var prefix2 = new RecipientNamePrefix(NAME_PREFIX);

        assertThat(prefix1).isEqualTo(prefix2);
        assertThat(prefix1.hashCode()).isEqualTo(prefix2.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenValuesDiffer() {
        var prefix1 = new RecipientNamePrefix("john");
        var prefix2 = new RecipientNamePrefix("jane");

        assertThat(prefix1).isNotEqualTo(prefix2);
    }
}
