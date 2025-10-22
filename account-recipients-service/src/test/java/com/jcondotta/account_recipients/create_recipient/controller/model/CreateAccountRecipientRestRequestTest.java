package com.jcondotta.account_recipients.create_recipient.controller.model;

import com.jcondotta.account_recipients.common.argument_provider.BlankValuesArgumentProvider;
import com.jcondotta.account_recipients.common.factory.ValidatorTestFactory;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThat;

class CreateAccountRecipientRestRequestTest {

    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

    private static final String VALID_NAME = AccountRecipientFixtures.JEFFERSON.getRecipientName();
    private static final String VALID_IBAN = AccountRecipientFixtures.JEFFERSON.getRecipientIban();

    @Test
    void shouldNotDetectAnyViolation_whenAllFieldsAreValid() {
        var request = new CreateAccountRecipientRestRequest(VALID_NAME, VALID_IBAN);
        assertThat(VALIDATOR.validate(request)).isEmpty();
    }

    @Test
    void shouldNotDetectAnyViolationUsingFactoryMethod_whenAllFieldsAreValid() {
        var request = CreateAccountRecipientRestRequest.of(VALID_NAME, VALID_IBAN);
        assertThat(VALIDATOR.validate(request)).isEmpty();
    }

    @ParameterizedTest
    @NullSource
    @ArgumentsSource(BlankValuesArgumentProvider.class)
    void shouldDetectConstraintViolation__whenRecipientNameIsBlank(String invalidName) {
        var request = new CreateAccountRecipientRestRequest(invalidName, VALID_IBAN);

        assertThat(VALIDATOR.validate(request))
            .hasSize(1)
            .first()
            .satisfies(v -> {
                assertThat(v.getPropertyPath().toString()).isEqualTo("recipientName");
                assertThat(v.getMessage()).isEqualTo("must not be blank");
            });
    }

    @ParameterizedTest
    @NullSource
    @ArgumentsSource(BlankValuesArgumentProvider.class)
    void shouldDetectConstraintViolation__whenIbanIsBlank(String invalidIban) {
        var request = new CreateAccountRecipientRestRequest(VALID_NAME, invalidIban);

        assertThat(VALIDATOR.validate(request))
            .hasSize(1)
            .first()
            .satisfies(v -> {
                assertThat(v.getPropertyPath().toString()).isEqualTo("iban");
                assertThat(v.getMessage()).isEqualTo("must not be blank");
            });
    }
}
