package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model;

import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetRecipientsLastEvaluatedKeyTest {

  private UUID bankAccountId;
  private UUID recipientId;
  private String recipientName;

  @BeforeEach
  void setUp() {
    bankAccountId = UUID.randomUUID();
    recipientId = UUID.randomUUID();
    recipientName = AccountRecipientFixtures.JEFFERSON.getRecipientName();
  }

  @Test
  void shouldCreateInstance_whenAllValuesAreValid() {
    var key = new GetRecipientsLastEvaluatedKey(bankAccountId, recipientId, recipientName);

    assertThat(key.bankAccountId()).isEqualTo(bankAccountId);
    assertThat(key.recipientId()).isEqualTo(recipientId);
    assertThat(key.recipientName()).isEqualTo(recipientName);
  }

  @Test
  void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
    assertThatThrownBy(
        () -> new GetRecipientsLastEvaluatedKey(null, recipientId, recipientName))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("bank account id must not be null");
  }

  @Test
  void shouldThrowNullPointerException_whenRecipientIdIsNull() {
    assertThatThrownBy(() -> new GetRecipientsLastEvaluatedKey(bankAccountId, null, recipientName))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("account recipient id must not be null");
  }

  @Test
  void shouldThrowNullPointerException_whenRecipientNameIsNull() {
    assertThatThrownBy(
        () -> new GetRecipientsLastEvaluatedKey(bankAccountId, recipientId, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("recipient name must not be null");
  }

  @Test
  void shouldThrowIllegalArgumentException_whenRecipientNameIsBlank() {
    assertThatThrownBy(
        () -> new GetRecipientsLastEvaluatedKey(bankAccountId, recipientId, "   "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("recipient name must not be blank");
  }

  @Test
  void shouldBeEqual_whenValuesAreTheSame() {
    var k1 = new GetRecipientsLastEvaluatedKey(bankAccountId, recipientId, recipientName);
    var k2 = new GetRecipientsLastEvaluatedKey(bankAccountId, recipientId, recipientName);

    assertThat(k1).isEqualTo(k2);
    assertThat(k1).hasSameHashCodeAs(k2);
  }

  @Test
  void shouldNotBeEqual_whenValuesDiffer() {
    var k1 = new GetRecipientsLastEvaluatedKey(bankAccountId, recipientId, recipientName);
    var k2 =
        new GetRecipientsLastEvaluatedKey(bankAccountId, recipientId, "Other Recipient");

    assertThat(k1).isNotEqualTo(k2);
  }

  @Test
  void shouldContainValuesInToString() {
    var key = new GetRecipientsLastEvaluatedKey(bankAccountId, recipientId, recipientName);

    assertThat(key.toString())
        .contains(bankAccountId.toString())
        .contains(recipientId.toString())
        .contains(recipientName);
  }

  @Test
  void shouldCreateInstanceUsingFactoryMethod() {
    var key = GetRecipientsLastEvaluatedKey.of(bankAccountId, recipientId, recipientName);

    assertThat(key.bankAccountId()).isEqualTo(bankAccountId);
    assertThat(key.recipientId()).isEqualTo(recipientId);
    assertThat(key.recipientName()).isEqualTo(recipientName);
  }
}
