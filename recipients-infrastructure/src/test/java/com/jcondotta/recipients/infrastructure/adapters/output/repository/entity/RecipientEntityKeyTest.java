package com.jcondotta.recipients.infrastructure.adapters.output.repository.entity;

import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientEntityKeyTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final UUID RECIPIENT_UUID = UUID.randomUUID();

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);
  private static final RecipientId ACCOUNT_RECIPIENT_ID =
      RecipientId.of(RECIPIENT_UUID);

  @Test
  void shouldBuildPartitionKey_fromUuid() {
    var pk = RecipientEntityKey.partitionKey(BANK_ACCOUNT_UUID);

    assertThat(pk).isEqualTo("ACCOUNT_OWNER#" + BANK_ACCOUNT_UUID);
  }

  @Test
  void shouldBuildPartitionKey_fromBankAccountId() {
    var pk = RecipientEntityKey.partitionKey(BANK_ACCOUNT_ID);

    assertThat(pk).isEqualTo("ACCOUNT_OWNER#" + BANK_ACCOUNT_UUID);
  }

  @Test
  void shouldBuildSortKey_fromUuid() {
    var sk = RecipientEntityKey.sortKey(RECIPIENT_UUID);

    assertThat(sk).isEqualTo("ACCOUNT_RECIPIENT#" + RECIPIENT_UUID);
  }

  @Test
  void shouldBuildSortKey_fromRecipientId() {
    var sk = RecipientEntityKey.sortKey(ACCOUNT_RECIPIENT_ID);

    assertThat(sk).isEqualTo("ACCOUNT_RECIPIENT#" + RECIPIENT_UUID);
  }

  @Test
  void shouldExtractBankAccountId_fromValidPartitionKey() {
    var pk = "ACCOUNT_OWNER#" + BANK_ACCOUNT_UUID;

    var result = RecipientEntityKey.extractBankAccountId(pk);

    assertThat(result).isEqualTo(BANK_ACCOUNT_ID);
  }

  @Test
  void shouldExtractRecipientId_fromValidSortKey() {
    var sk = "ACCOUNT_RECIPIENT#" + RECIPIENT_UUID;

    var result = RecipientEntityKey.extractRecipientId(sk);

    assertThat(result).isEqualTo(ACCOUNT_RECIPIENT_ID);
  }

  @Test
  void shouldThrowException_whenPartitionKeyIsNull() {
    assertThatThrownBy(() -> RecipientEntityKey.extractBankAccountId(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid partitionKey: null");
  }

  @Test
  void shouldThrowException_whenPartitionKeyHasInvalidPrefix() {
    var invalidPk = "WRONG#" + BANK_ACCOUNT_UUID;

    assertThatThrownBy(() -> RecipientEntityKey.extractBankAccountId(invalidPk))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid partitionKey");
  }

  @Test
  void shouldThrowException_whenSortKeyIsNull() {
    assertThatThrownBy(() -> RecipientEntityKey.extractRecipientId(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid sortKey: null");
  }

  @Test
  void shouldThrowException_whenSortKeyHasInvalidPrefix() {
    var invalidSk = "WRONG#" + RECIPIENT_UUID;

    assertThatThrownBy(() -> RecipientEntityKey.extractRecipientId(invalidSk))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid sortKey");
  }

  @Test
  void shouldThrowException_whenPartitionKeyHasInvalidUUID() {
    var invalidPk = "ACCOUNT_OWNER#not-a-uuid";

    assertThatThrownBy(() -> RecipientEntityKey.extractBankAccountId(invalidPk))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid UUID string");
  }

  @Test
  void shouldThrowException_whenSortKeyHasInvalidUUID() {
    var invalidSk = "ACCOUNT_RECIPIENT#1234";

    assertThatThrownBy(() -> RecipientEntityKey.extractRecipientId(invalidSk))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid UUID string");
  }
}
