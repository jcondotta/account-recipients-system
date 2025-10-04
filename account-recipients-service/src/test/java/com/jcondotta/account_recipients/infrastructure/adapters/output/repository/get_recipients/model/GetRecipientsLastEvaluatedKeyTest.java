package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model;

import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetRecipientsLastEvaluatedKeyTest {

    private UUID bankAccountId;
    private UUID accountRecipientId;
    private String recipientName;

    @BeforeEach
    void setUp() {
        bankAccountId = UUID.randomUUID();
        accountRecipientId = UUID.randomUUID();
        recipientName = AccountRecipientFixtures.JEFFERSON.getRecipientName();
    }

    @Test
    void shouldCreateInstance_whenAllValuesAreValid() {
        var key = new GetRecipientsLastEvaluatedKey(bankAccountId, accountRecipientId, recipientName);

        assertThat(key.bankAccountId()).isEqualTo(bankAccountId);
        assertThat(key.accountRecipientId()).isEqualTo(accountRecipientId);
        assertThat(key.recipientName()).isEqualTo(recipientName);
    }

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
        assertThatThrownBy(() -> new GetRecipientsLastEvaluatedKey(null, accountRecipientId, recipientName))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("bank account id must not be null");
    }

    @Test
    void shouldThrowNullPointerException_whenAccountRecipientIdIsNull() {
        assertThatThrownBy(() -> new GetRecipientsLastEvaluatedKey(bankAccountId, null, recipientName))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("account recipient id must not be null");
    }

    @Test
    void shouldThrowNullPointerException_whenRecipientNameIsNull() {
        assertThatThrownBy(() -> new GetRecipientsLastEvaluatedKey(bankAccountId, accountRecipientId, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("recipient name must not be null");
    }

    @Test
    void shouldThrowIllegalArgumentException_whenRecipientNameIsBlank() {
        assertThatThrownBy(() -> new GetRecipientsLastEvaluatedKey(bankAccountId, accountRecipientId, "   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("recipient name must not be blank");
    }

    @Test
    void shouldBeEqual_whenValuesAreTheSame() {
        var k1 = new GetRecipientsLastEvaluatedKey(bankAccountId, accountRecipientId, recipientName);
        var k2 = new GetRecipientsLastEvaluatedKey(bankAccountId, accountRecipientId, recipientName);

        assertThat(k1).isEqualTo(k2);
        assertThat(k1.hashCode()).isEqualTo(k2.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenValuesDiffer() {
        var k1 = new GetRecipientsLastEvaluatedKey(bankAccountId, accountRecipientId, recipientName);
        var k2 = new GetRecipientsLastEvaluatedKey(bankAccountId, accountRecipientId, "Other Recipient");

        assertThat(k1).isNotEqualTo(k2);
    }

    @Test
    void shouldContainValuesInToString() {
        var key = new GetRecipientsLastEvaluatedKey(bankAccountId, accountRecipientId, recipientName);

        assertThat(key.toString())
            .contains(bankAccountId.toString())
            .contains(accountRecipientId.toString())
            .contains(recipientName);
    }

    @Test
    void shouldCreateInstanceUsingFactoryMethod() {
        var key = GetRecipientsLastEvaluatedKey.of(bankAccountId, accountRecipientId, recipientName);

        assertThat(key.bankAccountId()).isEqualTo(bankAccountId);
        assertThat(key.accountRecipientId()).isEqualTo(accountRecipientId);
        assertThat(key.recipientName()).isEqualTo(recipientName);
    }
}