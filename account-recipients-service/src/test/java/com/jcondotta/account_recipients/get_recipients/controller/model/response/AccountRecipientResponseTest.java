package com.jcondotta.account_recipients.get_recipients.controller.model.response;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AccountRecipientResponse}.
 */
class AccountRecipientResponseTest {

    private static final UUID ACCOUNT_RECIPIENT_ID = UUID.randomUUID();
    private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();
    private static final String RECIPIENT_NAME = "Jefferson Condotta";
    private static final String IBAN = "ES9820385778983000760236";

    @Test
    void shouldCreateResponse_whenAllFieldsAreProvided() {
        var response = AccountRecipientResponse.of(ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN);

        assertThat(response)
            .satisfies(it -> {
                assertThat(it.accountRecipientId()).isEqualTo(ACCOUNT_RECIPIENT_ID);
                assertThat(it.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
                assertThat(it.recipientName()).isEqualTo(RECIPIENT_NAME);
                assertThat(it.iban()).isEqualTo(IBAN);
            });
    }

    @Test
    void shouldHaveValueEquality_whenFieldsAreIdentical() {
        var response1 = AccountRecipientResponse.of(ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN);
        var response2 = AccountRecipientResponse.of(ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN);

        assertThat(response1)
            .isEqualTo(response2)
            .hasSameHashCodeAs(response2);
    }
}
