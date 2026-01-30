package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.delete_recipient;

import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.exceptions.AccountRecipientNotFoundException;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAccountRecipientRepositoryImplTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();
  private static final RecipientName RECIPIENT_NAME_JEFFERSON = RecipientName.of("Jefferson Condotta");
  private static final Iban IBAN = Iban.of("GB82WEST12345698765432");

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2022-06-24T12:45:01Z"), ZoneOffset.UTC);
  private static final ZonedDateTime CREATED_AT = ZonedDateTime.now(CLOCK);

  @Mock
  private DynamoDbTable<AccountRecipientEntity> dynamoDbTable;

  @InjectMocks
  private DeleteAccountRecipientRepositoryImpl deleteRepository;

  @Captor
  private ArgumentCaptor<Consumer<DeleteItemEnhancedRequest.Builder>> deleteItemConsumerCaptor;

  @Test
  void shouldDeleteRecipientSuccessfully_whenAccountRecipientExists() {
    var accountRecipient =
        AccountRecipient.restore(
            RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CREATED_AT);

    deleteRepository.delete(accountRecipient);

    verify(dynamoDbTable).deleteItem(deleteItemConsumerCaptor.capture());

    var builder = DeleteItemEnhancedRequest.builder();
    deleteItemConsumerCaptor.getValue().accept(builder);
    var request = builder.build();

    assertThat(request.key().partitionKeyValue().s())
        .hasToString(AccountRecipientEntityKey.partitionKey(BANK_ACCOUNT_ID));

    assertThat(request.key().sortKeyValue())
        .hasValueSatisfying(
            attr ->
                assertThat(attr.s())
                    .isEqualTo(AccountRecipientEntityKey.sortKey(RECIPIENT_ID)));

    assertThat(request.conditionExpression()).isNotNull();
    assertThat(request.conditionExpression().expression())
        .isEqualTo("attribute_exists(partitionKey) AND attribute_exists(sortKey)");

    verifyNoMoreInteractions(dynamoDbTable);
  }

  @Test
  void shouldThrowAccountRecipientNotFoundException_whenRecipientDoesNotExist() {
    var accountRecipient =
        AccountRecipient.restore(
            RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CREATED_AT);

    doThrow(ConditionalCheckFailedException.builder().build())
        .when(dynamoDbTable)
        .deleteItem(ArgumentMatchers.<Consumer<DeleteItemEnhancedRequest.Builder>>any());

    assertThatThrownBy(() -> deleteRepository.delete(accountRecipient))
        .isInstanceOfSatisfying(
            AccountRecipientNotFoundException.class,
            ex ->
                assertThat(ex.getIdentifiers())
                    .containsExactlyInAnyOrder(
                        BANK_ACCOUNT_ID.value(), RECIPIENT_ID.value()));

    verify(dynamoDbTable).deleteItem(ArgumentMatchers.<Consumer<DeleteItemEnhancedRequest.Builder>>any());
  }
}